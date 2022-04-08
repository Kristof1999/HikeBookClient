package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailMapBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
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

        map = binding.groupsMapMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setStartZoomAndCenter()

        val groupName = arguments?.getString("groupName")!!

        binding.groupsMapCreateRouteFab.setOnClickListener {
            val routeType = RouteType.GROUP
            val directions = GroupsDetailFragmentDirections
                .actionGroupsDetailFragmentToRouteCreateFragment(routeType, groupName, null)
            findNavController(requireActivity(), R.id.navHostFragment).navigate(directions)
        }

        val viewModel: GroupsDetailMapViewModel by viewModels()
        viewModel.loadRoutesOfGroup(groupName)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routes.observe(viewLifecycleOwner) { routes ->
            MapUtils.onRoutesLoad(routes, context, map)
        }

        map.invalidate()
    }
}