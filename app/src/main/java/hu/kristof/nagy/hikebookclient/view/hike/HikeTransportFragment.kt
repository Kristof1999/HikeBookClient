// based on:
// https://github.com/MKergall/osmbonuspack/wiki/Tutorial_1

package hu.kristof.nagy.hikebookclient.view.hike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikeTransportBinding
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikeTransportViewModel
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class HikeTransportFragment : Fragment() {
    private lateinit var binding: FragmentHikeTransportBinding
    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_hike_transport, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

        val viewModel: HikeTransportViewModel by viewModels()
        val args: HikeTransportFragmentArgs by navArgs()
        val roadManager = OSRMRoadManager(context, BuildConfig.APPLICATION_ID)
        viewModel.getRoad(args, roadManager)

        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.roadRes.observe(viewLifecycleOwner) { road ->
            onRoadResult(road)
        }

        binding.hikeTransportFinishButton.setOnClickListener {
            if (args.isForward) {
                val directions = HikeTransportFragmentDirections
                    .actionHikeTransportFragmentToHikeFragment(args.userRoute)
                findNavController().navigate(directions)
            } else {
                findNavController().navigate(
                    R.id.action_hikeTransportFragment_to_myMapFragment
                )
            }
        }
    }

    private fun onRoadResult(road: Road) {
        val roadOverlay = RoadManager.buildRoadOverlay(road)

        val nodeIcon = AppCompatResources.getDrawable(
            requireContext(), org.osmdroid.wms.R.drawable.marker_default
        )
        for (i in road.mNodes.indices) {
            val node = road.mNodes[i]
            val nodeMarker = Marker(map)
            nodeMarker.position = node.mLocation
            nodeMarker.icon = nodeIcon
            nodeMarker.title = "Step $i"
            nodeMarker.snippet = node.mInstructions
            nodeMarker.subDescription =
                Road.getLengthDurationText(requireContext(), node.mLength, node.mDuration)
            map.overlays.add(nodeMarker)
        }

        map.getOverlays().add(roadOverlay)
        map.invalidate()
    }

    private fun initMap() {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        map = binding.hikeTransportMap
        // TODO: use different map sources based on chosen transport mean
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()
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