package hu.kristof.nagy.hikebookclient.view.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.SwitchCompat
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.MapFragment
import hu.kristof.nagy.hikebookclient.util.getMarkerIcon
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

abstract class RouteFragment : MapFragment(), AdapterView.OnItemSelectedListener {
    protected abstract val viewModel: RouteViewModel
    protected abstract val switch: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            val makeMyMarker = { point: Point ->
                val marker = Marker(map)
                val markerType = point.type
                val myMarker = MyMarker(marker, markerType, point.title)
                marker.customize(
                    myMarker.title,
                    getMarkerIcon(markerType, resources),
                    point.toGeoPoint()
                )
                marker.setListeners(
                    requireContext(), map, switch, viewModel
                )
                myMarker
            }

            val points = markers.map { Point.from(it) }
            makeMyMarker(points.first()).let { myFirstMarker ->
                map.overlays.add(myFirstMarker.marker)
            }
            for (point in points.subList(1, points.size)) {
                makeMyMarker(point).let { myMarker ->
                    map.overlays.add(myMarker.marker)
                }

                makePolylineFromLastTwo(markers).let { polyline ->
                    map.overlays.add(polyline)
                }
            }

            map.invalidate()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected fun makePolylineFromLastTwo(
        markers: List<MyMarker>
    ): Polyline = Polyline().apply {
        setPoints(listOf(
            markers[markers.size - 2].marker.position,
            markers[markers.size - 1].marker.position
        ))
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        onMarkerItemSelected(pos, viewModel, parentFragmentManager, viewLifecycleOwner)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }
}