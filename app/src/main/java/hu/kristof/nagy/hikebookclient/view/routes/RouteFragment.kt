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

/**
 * A MapFragment that keeps the view up to date regarding the markers
 * of the loaded route and those which have been placed so far.
 * It also handles a switch's item selected event.
 */
abstract class RouteFragment : MapFragment(), AdapterView.OnItemSelectedListener {
    protected abstract val viewModel: RouteViewModel
    protected abstract val switch: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            val makeMarker = { point: Point ->
                val marker = Marker(map)
                marker.customize(
                    point.title,
                    getMarkerIcon(point.type, resources),
                    point.toGeoPoint()
                )
                marker.setListeners(
                    requireContext(), map, switch, viewModel
                )
                marker
            }

            val points = markers.map { Point.from(it) }

            if (points.isEmpty()) {
                return@observe
            } else {
                makeMarker(points.first()).let { firstMarker ->
                    map.overlays.add(firstMarker)
                }

                for (point in points.subList(1, points.size)) {
                    makeMarker(point).let { marker ->
                        map.overlays.add(marker)
                    }

                    makePolylineFromLastTwo(markers).let { polyline ->
                        map.overlays.add(polyline)
                    }
                }

                map.invalidate()
            }
        }

        return null
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