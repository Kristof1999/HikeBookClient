package hu.kristof.nagy.hikebookclient.view.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.SwitchCompat
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.MapFragment
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.getMarkerIcon
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Marker

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

        map.overlays.clear()

        val points = viewModel.markers.map { Point.from(it) }
        for (point in points) {
            val marker = Marker(map)
            marker.customize(
                point.title,
                getMarkerIcon(point.type, resources),
                point.toGeoPoint()
            )
            marker.setListeners(
                requireContext(), map, switch, viewModel
            )
            map.overlays.add(marker)
        }

        for (polyline in viewModel.polylines) {
            map.overlays.add(polyline)
        }

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.addCopyRightOverlay()
        map.setStartZoomAndCenter()
        setMapClickListeners(requireContext(), map, switch, viewModel)
        map.invalidate()

        return null
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        onMarkerItemSelected(pos, viewModel, parentFragmentManager, viewLifecycleOwner)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }
}