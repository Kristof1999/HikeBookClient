package hu.kristof.nagy.hikebookclient.view.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.SwitchCompat
import hu.kristof.nagy.hikebookclient.util.MapFragment
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel

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
        if (map.overlays.isEmpty()) {
            setMapClickListeners(requireContext(), map, switch, viewModel)
        } else {
            val eventsOverlay = map.overlays.removeFirst()
            map.overlays.clear()
            map.overlays.add(0, eventsOverlay)

            val markers = viewModel.markers.map { it.marker }
            for (marker in markers) {
                map.overlays.add(marker)
            }

            for (polyline in viewModel.polylines) {
                map.overlays.add(polyline)
            }

            map.invalidate()
        }

        return null
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        onMarkerItemSelected(pos, viewModel, parentFragmentManager, viewLifecycleOwner)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }
}