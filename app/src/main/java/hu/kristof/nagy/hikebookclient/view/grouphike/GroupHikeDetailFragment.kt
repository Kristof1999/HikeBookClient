package hu.kristof.nagy.hikebookclient.view.grouphike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupHikeDetailBinding
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.viewModel.grouphike.GroupHikeDetailViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

@AndroidEntryPoint
class GroupHikeDetailFragment : MapFragment() {
    private lateinit var binding: FragmentGroupHikeDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_group_hike_detail, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: GroupHikeDetailFragmentArgs by navArgs()
        adaptView(args)

        val viewModel: GroupHikeDetailViewModel by viewModels()
        binding.lifecycleOwner = viewLifecycleOwner

        setupGeneralConnect(viewModel, args)

        setupAddToMyMap(viewModel)

        viewModel.route.observe(viewLifecycleOwner) { route ->
            showRoutePolylineOnMap(route)

            showRoutePointsOnMap(route)

            closeInfoWindows()

            binding.groupHikeDetailDescriptionTv.text = route.description

            map.invalidate()
        }
        handleOfflineLoad(requireContext()) {
            viewModel.loadRoute(args.groupHikeName)
            setupRecyclerView(viewModel, args)
        }

        initMap()
    }

    private fun closeInfoWindows() {
        MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                InfoWindow.closeAllInfoWindowsOn(map)
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return true
            }
        }).also { mapEventsOverlay ->
            map.overlays.add(0, mapEventsOverlay)
        }
    }

    private fun showRoutePointsOnMap(route: Route) {
        for (point in route.points) {
            Marker(map).apply {
                setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
                icon = MarkerUtils.getMarkerIcon(point.type, resources)
                title = point.title
                position = point.toGeoPoint()
            }.also { marker ->
                map.overlays.add(marker)
            }
        }
    }

    private fun showRoutePolylineOnMap(route: Route) {
        route.toPolyline().apply {
            setOnClickListener { _, _, _ ->
                Toast.makeText(context, route.routeName, Toast.LENGTH_SHORT).show()
                return@setOnClickListener true
            }
        }.also { polyline ->
            map.overlays.add(polyline)
            map.setMapCenterOnPolylineCenter(polyline)
        }
    }

    private fun setupAddToMyMap(viewModel: GroupHikeDetailViewModel) {
        binding.groupHikeDetailAddToMyMapButton.setOnClickListener {
            catchAndShowIllegalStateAndArgument(requireContext()) {
                handleOffline(requireContext()) {
                    viewModel.addToMyMap()
                }
            }
        }
        viewModel.addToMyMapRes.observe(viewLifecycleOwner) { res ->
            if (!viewModel.addToMyMapFinished) {
                handleResult(context, res) { addToMyMapRes ->
                    showGenericErrorOr(context, addToMyMapRes, "A felvétel sikeres!")
                }
                viewModel.addToMyMapFinished = true
            }
        }
    }

    private fun setupGeneralConnect(
        viewModel: GroupHikeDetailViewModel,
        args: GroupHikeDetailFragmentArgs
    ) {
        binding.groupHikeDetailGeneralConnectButton.setOnClickListener {
            viewModel.generalConnect(args.groupHikeName, args.isConnectedPage, args.dateTime)
        }
        viewModel.generalConnectRes.observe(viewLifecycleOwner) { generalConnectRes ->
            showGenericErrorOr(context, generalConnectRes) {
                findNavController().navigate(
                    R.id.action_groupHikeDetailFragment_to_groupHikeFragment
                )
            }
        }
    }

    private fun setupRecyclerView(
        viewModel: GroupHikeDetailViewModel,
        args: GroupHikeDetailFragmentArgs
    ) {
        val adapter = GroupHikeDetailParticipantsListAdapter()
        viewModel.participants.observe(viewLifecycleOwner) { participants ->
            adapter.submitList(participants.toMutableList())
        }
        binding.groupHikeDetailRecyclerView.adapter = adapter
        viewModel.listParticipants(args.groupHikeName)
    }

    private fun initMap() {
        map = binding.groupHikeDetailMap.apply {
            setStartZoomAndCenter()
            setTileSource(TileSourceFactory.MAPNIK)
            addCopyRightOverlay()
        }
    }

    private fun adaptView(args: GroupHikeDetailFragmentArgs) = binding.apply {
        groupHikeDetailNameTv.text = args.groupHikeName
        groupHikeDetailGeneralConnectButton.apply {
            if (args.isConnectedPage) {
                text = "Elhagyás"
            } else {
                text = "Csatlakozás"
            }
        }
    }
}