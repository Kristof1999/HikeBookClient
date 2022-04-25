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
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Marker

/**
 * A MapFragment to display the planned route on a map.
 * It has a button to stop travelling, and start hiking
 * or go back to my map screen.
 */
class HikeTransportFragment : MapFragment() {
    private lateinit var binding: FragmentHikeTransportBinding
    private val viewModel: HikeTransportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHikeTransportBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        setupObserver()

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObserver() {
        viewModel.roadRes.observe(viewLifecycleOwner) { road ->
            onRoadResult(road)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

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

    private fun initMap() {
        map = binding.hikeTransportMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
            addCopyRightOverlay()
        }
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