package hu.kristof.nagy.hikebookclient.view.grouphike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupHikeDetailBinding
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.grouphike.GroupHikeDetailViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
 * A MapFragment that displays the details of a group hike list item.
 * It displays the route on a map, the group hike's name,
 * the route's description, the list of participants.
 * It has a button with which the user can join or leave the group hike,
 * and another button to add the given route to his/her map.
 */
@AndroidEntryPoint
class GroupHikeDetailFragment : MapFragment() {
    private lateinit var binding: FragmentGroupHikeDetailBinding
    private val viewModel: GroupHikeDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupHikeDetailBinding.inflate(layoutInflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        setupObservers()

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObservers() {
        with(viewModel) {
            addToMyMapRes.observe(viewLifecycleOwner) { res ->
                if (!viewModel.addToMyMapFinished) {
                    handleResult(context, res) { addToMyMapRes ->
                        showGenericErrorOr(context, addToMyMapRes, "A felvétel sikeres!")
                    }
                    viewModel.addToMyMapFinished = true
                }
            }
            generalConnectRes.observe(viewLifecycleOwner) { res ->
                handleResult(context, res) { generalConnectRes ->
                    showGenericErrorOr(context, generalConnectRes) {
                        findNavController().navigate(
                            R.id.action_groupHikeDetailFragment_to_groupHikeFragment
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: GroupHikeDetailFragmentArgs by navArgs()
        adaptView(args)

        setupGeneralConnect(viewModel, args)

        setupAddToMyMap(viewModel)

        setupLoad(viewModel, args)

        initMap()
    }

    private fun setupLoad(
        viewModel: GroupHikeDetailViewModel,
        args: GroupHikeDetailFragmentArgs
    ) {
        viewModel.route.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { route ->
                showRoutePolylineOnMap(route)

                showRoutePointsOnMap(route)

                closeInfoWindows()

                binding.groupHikeDetailDescriptionTv.text = route.description

                map.invalidate()
            }
        }
        setupList(viewModel, args)
        handleOfflineLoad(requireContext()) {
            viewModel.loadRoute(args.groupHikeName)
        }
    }

    private fun closeInfoWindows() = MapEventsOverlay(object : MapEventsReceiver {
        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
            InfoWindow.closeAllInfoWindowsOn(map)
            return true
        }

        override fun longPressHelper(p: GeoPoint?): Boolean {
            return true
        }
    }).let { mapEventsOverlay ->
        map.overlays.add(0, mapEventsOverlay)
    }

    private fun showRoutePointsOnMap(route: Route) {
        for (point in route.points) {
            Marker(map).apply {
                setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
                icon = getMarkerIcon(point.type, resources)
                title = point.title
                position = point.toGeoPoint()
            }.let { marker ->
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
        }.let { polyline ->
            map.overlays.add(polyline)
            map.setMapCenterOnPolylineCenter(polyline)
        }
    }

    private fun setupAddToMyMap(viewModel: GroupHikeDetailViewModel) {
        binding.groupHikeDetailAddToMyMapButton.setOnClickListener {
            handleOffline(requireContext()) {
                catchAndShowIllegalStateAndArgument(requireContext()) {
                    viewModel.addToMyMap()
                }
            }
        }
    }

    private fun setupGeneralConnect(
        viewModel: GroupHikeDetailViewModel,
        args: GroupHikeDetailFragmentArgs
    ) {
        binding.groupHikeDetailGeneralConnectButton.setOnClickListener {
            handleOffline(requireContext()) {
                viewModel.generalConnect(args.groupHikeName, args.isConnectedPage, args.dateTime)
            }
        }
    }

    private fun setupList(
        viewModel: GroupHikeDetailViewModel,
        args: GroupHikeDetailFragmentArgs
    ) {
        val adapter = GroupHikeDetailParticipantsListAdapter()
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.participants.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { participants ->
                adapter.submitList(participants.toMutableList())
            }
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

    private fun adaptView(args: GroupHikeDetailFragmentArgs) = with(binding) {
        groupHikeDetailNameTv.text = args.groupHikeName
        groupHikeDetailGeneralConnectButton.apply {
            text = if (args.isConnectedPage) {
                "Elhagyás"
            } else {
                "Csatlakozás"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.GROUP_HIKE_DETAIL
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}