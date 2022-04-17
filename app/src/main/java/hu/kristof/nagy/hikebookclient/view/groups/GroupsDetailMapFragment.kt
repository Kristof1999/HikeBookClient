package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailMapBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMapViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

/**
 * A Fragment which displays the routes of a group on a map.
 * It has 2 buttons:
 * one to create a route for the group, and
 * one to add a route to the group from the user's map.
 */
@AndroidEntryPoint
class GroupsDetailMapFragment : MapFragment() {
    private lateinit var binding: FragmentGroupsDetailMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_groups_detail_map, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

        val groupName = arguments?.getString(Constants.GROUP_NAME_BUNDLE_KEY)!!
        adaptView()

        binding.groupsMapCreateRouteFab.setOnClickListener {
            onRouteCreate(groupName)
        }

        val viewModel: GroupsDetailMapViewModel by activityViewModels()
        handleOfflineLoad(requireContext()) {
            setupLoad(viewModel, groupName)
        }

        setupAddFromMyMap(viewModel, groupName)

        map.invalidate()
    }

    private fun setupAddFromMyMap(
        viewModel: GroupsDetailMapViewModel,
        groupName: String
    ) {
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.addFromMyMapRes.observe(viewLifecycleOwner) { res ->
            if (!viewModel.addFromMyMapFinished) {
                handleResult(context, res) { addFromMyMapRes ->
                    showGenericErrorOr(context, addFromMyMapRes, "A felvétel sikeres!")
                }
                viewModel.addFromMyMapFinished = true
            }
        }

        val dialog = AddFromMyMapDialogFragment()
        dialog.route.observe(viewLifecycleOwner) { route ->
            viewModel.onAddFromMyMap(route, groupName)
        }
        binding.groupsMapAddFromMyMapButton.setOnClickListener {
            handleOffline(requireContext()) {
                dialog.show(parentFragmentManager, "add from my map")
            }
        }
    }

    private fun setupLoad(
        viewModel: GroupsDetailMapViewModel,
        groupName: String
    ) {
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routes.observe(viewLifecycleOwner) { routes ->
            MapUtils.onRoutesLoad(routes, context, map)
        }
        viewModel.loadRoutesOfGroup(groupName)
    }

    private fun onRouteCreate(groupName: String) {
        val routeType = RouteType.GROUP
        val directions = GroupsDetailFragmentDirections
            .actionGroupsDetailFragmentToRouteCreateFragment(routeType, groupName)
        findNavController(requireActivity(), R.id.navHostFragment).navigate(directions)
    }

    private fun adaptView() {
        val isConnectedPage = arguments?.getBoolean(Constants.IS_CONNECTED_PAGE_BUNDLE_KEY)!!
        if (!isConnectedPage) {
            binding.groupsMapCreateRouteFab.isVisible = false
            binding.groupsMapAddFromMyMapButton.isVisible = false
        }
    }

    private fun initMap() {
        map = binding.groupsMapMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
        }
    }
}