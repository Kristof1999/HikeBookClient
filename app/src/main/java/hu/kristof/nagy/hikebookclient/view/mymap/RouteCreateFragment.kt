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
                if (isDeleteOn) {
                    //TODO: súgóba: törlés közben nem vehetünk fel új pontokat
                    return true
                }
                // add new marker
                val newMarker = Marker(map)
                newMarker.setOnMarkerClickListener(Marker.OnMarkerClickListener { marker, mapView ->
                    if (isDeleteOn) {
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
                    return@OnMarkerClickListener true
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
                TODO("Not yet implemented")
            }
        })
        map.overlays.add(mapEventsOverlay)
        map.invalidate()
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