// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki

package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentRouteCreateBinding
import hu.kristof.nagy.hikebookclient.MapHelper
import hu.kristof.nagy.hikebookclient.model.Constants
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class RouteCreateFragment : Fragment() {
    private lateinit var map: MapView
    private lateinit var binding: FragmentRouteCreateBinding
    private var isDeleteOn = false // TODO: store in viewModel, handle interruptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_create, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.routeCreateMap

        binding.routeCreateDeleteSwitch.setOnClickListener {
            isDeleteOn = !isDeleteOn
        }

        val mapController = map.controller
        mapController.setZoom(Constants.START_ZOOM)
        mapController.setCenter(Constants.START_POINT)
        val markers = ArrayList<Marker>()
        val polylines = ArrayList<Polyline>()
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                //TODO: súgóba: törlés közben nem vehetünk fel új pontokat
                if (isDeleteOn)
                    return true

                // add new marker
                val newMarker = Marker(map)
                newMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
                newMarker.isDraggable = true
                newMarker.setOnMarkerClickListener(Marker.OnMarkerClickListener { marker, mapView ->
                    if (isDeleteOn) {
                        onDelete(marker, mapView, markers, polylines)
                    }
                    return@OnMarkerClickListener true
                })

                newMarker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                    override fun onMarkerDrag(marker: Marker?) {

                    }

                    override fun onMarkerDragEnd(marker: Marker?) {
                        if (markers.size == 1)
                            return

                        if (marker == null)
                            return

                        val idx = markers.indexOf(marker)
                        if (idx == 0) {
                            val points = ArrayList<GeoPoint>()
                            points.add(marker.position)
                            points.add(markers[idx + 1].position)
                            polylines[idx].setPoints(points)
                            polylines[idx].isVisible = true
                        } else if (idx == markers.size - 1) {
                            val points = ArrayList<GeoPoint>()
                            points.add(markers[idx - 1].position)
                            points.add(marker.position)
                            polylines[idx - 1].setPoints(points)
                            polylines[idx - 1].isVisible = true
                        } else {
                            val prevPoints = ArrayList<GeoPoint>()
                            prevPoints.add(markers[idx - 1].position)
                            prevPoints.add(marker.position)
                            polylines[idx - 1].setPoints(prevPoints)
                            polylines[idx - 1].isVisible = true

                            val nextPoints = ArrayList<GeoPoint>()
                            nextPoints.add(marker.position)
                            nextPoints.add(markers[idx + 1].position)
                            polylines[idx].setPoints(nextPoints)
                            polylines[idx].isVisible = true
                        }
                        map.invalidate()
                    }

                    override fun onMarkerDragStart(marker: Marker?) {
                        if (markers.size == 1)
                            return

                        if (marker == null)
                            return

                        val idx = markers.indexOf(marker)
                        if (idx == 0) {
                            polylines[idx].isVisible = false
                        } else if (idx == markers.size - 1) {
                            polylines[idx - 1].isVisible = false
                        } else {
                            polylines[idx - 1].isVisible = false
                            polylines[idx].isVisible = false
                        }
                        map.invalidate()
                    }
                })
                newMarker.position = p
                newMarker.icon = requireActivity().getDrawable(R.drawable.marker_image)
                markers.add(newMarker)
                map.overlays.add(newMarker)

                if (markers.size > 1) {
                    // change previous marker's icon
                    val prevMarker = markers[markers.size - 2]
                    prevMarker.icon = requireActivity().getDrawable(R.drawable.set_marker_image)

                    // connect the new point with the previous one
                    val points = ArrayList<GeoPoint>()
                    points.add(prevMarker.position)
                    points.add(newMarker.position)
                    val polyline = Polyline()
                    polyline.setPoints(points)
                    polylines.add(polyline)
                    map.overlays.add(polyline)
                }

                map.invalidate()
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                // itt (is) lehetne kiemelt pontok közül választani
                return true
            }
        })
        map.overlays.add(mapEventsOverlay)
        map.invalidate()
    }

    private fun onDelete(
        marker: Marker,
        mapView: MapView,
        markers: ArrayList<Marker>,
        polylines: ArrayList<Polyline>
    ) {
        if (markers.last() == marker) {
            marker.remove(mapView)
            markers.removeLast()
            if (markers.isNotEmpty()) {
                markers.last().icon = requireActivity().getDrawable(R.drawable.marker_image)
                polylines.last().isVisible = false
                polylines.removeLast()
            }
            mapView.invalidate()
        } else {
            Toast.makeText(
                context, "Csak a legutóbb felvett pontot lehet törölni.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MapHelper.onRequestPermissionsResult(
            requestCode, permissions, grantResults, requireActivity()
        )
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}