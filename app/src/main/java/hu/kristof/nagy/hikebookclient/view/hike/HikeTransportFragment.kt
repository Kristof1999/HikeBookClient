// based on:
// https://github.com/MKergall/osmbonuspack/wiki/Tutorial_1

package hu.kristof.nagy.hikebookclient.view.hike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikeTransportBinding
import hu.kristof.nagy.hikebookclient.util.MapFragment
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikeTransportViewModel
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
 * A MapFragment to display the planned route on a map.
 * It has a button to stop travelling, and start hiking
 * or go back to my map screen.
 */
class HikeTransportFragment : MapFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHikeTransportBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        val viewModel: HikeTransportViewModel by viewModels()

        setupObserver(viewModel)

        initMap(binding)

        val args: HikeTransportFragmentArgs by navArgs()

        val roadManager = OSRMRoadManager(context, BuildConfig.APPLICATION_ID)
        setupLoad(viewModel, args, roadManager)

        binding.hikeTransportFinishButton.setOnClickListener {
            if (args.isForward) {
                val directions = HikeTransportFragmentDirections
                    .actionHikeTransportFragmentToHikeFragment(args.routeName)
                findNavController().navigate(directions)
            } else {
                findNavController().navigate(
                    R.id.action_hikeTransportFragment_to_myMapFragment
                )
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObserver(viewModel: HikeTransportViewModel) {
        viewModel.roadRes.observe(viewLifecycleOwner) { road ->
            onRoadResult(road)
        }
    }

    private fun setupLoad(
        viewModel: HikeTransportViewModel,
        args: HikeTransportFragmentArgs,
        roadManager: OSRMRoadManager
    ) {
        handleOfflineLoad(requireContext()) {
            viewModel.getRoad(args, roadManager)
        }
    }

    private fun onRoadResult(road: Road) {
        val roadOverlay = RoadManager.buildRoadOverlay(road)

        val nodeIcon = AppCompatResources.getDrawable(
            requireContext(), org.osmdroid.wms.R.drawable.marker_default
        )
        for (i in road.mNodes.indices) {
            val node = road.mNodes[i]
            Marker(map).apply {
                position = node.mLocation
                icon = nodeIcon
                title = "$i. lépés"
                subDescription =
                    Road.getLengthDurationText(requireContext(), node.mLength, node.mDuration)
            }.let { nodeMarker ->
                map.overlays.add(nodeMarker)
            }
        }

        map.overlays.add(roadOverlay)
        map.invalidate()
    }

    private fun initMap(binding: FragmentHikeTransportBinding) {
        map = binding.hikeTransportMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
            addCopyRightOverlay()
        }
        addMapEventsOverlay()
    }

    private fun addMapEventsOverlay() {
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                InfoWindow.closeAllInfoWindowsOn(map)
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return true
            }
        })
        map.overlays.add(0, mapEventsOverlay)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.HIKE_TRANSPORT
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}