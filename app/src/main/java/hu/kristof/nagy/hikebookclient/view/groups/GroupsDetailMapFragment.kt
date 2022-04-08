package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailMapBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MapFragment
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMapViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

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

        val viewModel: GroupsDetailMapViewModel by viewModels()
        loadRoutesOfGroup(viewModel, groupName)

        map.invalidate()
    }

    private fun loadRoutesOfGroup(
        viewModel: GroupsDetailMapViewModel,
        groupName: String
    ) {
        viewModel.loadRoutesOfGroup(groupName)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routes.observe(viewLifecycleOwner) { routes ->
            MapUtils.onRoutesLoad(routes, context, map)
        }
    }

    private fun onRouteCreate(groupName: String) {
        val routeType = RouteType.GROUP
        val directions = GroupsDetailFragmentDirections
            .actionGroupsDetailFragmentToRouteCreateFragment(routeType, groupName, null)
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
        map = binding.groupsMapMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setStartZoomAndCenter()
    }
}