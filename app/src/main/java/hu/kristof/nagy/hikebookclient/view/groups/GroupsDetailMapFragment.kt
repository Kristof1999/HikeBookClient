package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailMapBinding
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.view.routes.RouteType
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay

@AndroidEntryPoint
class GroupsDetailMapFragment : Fragment() {
    private lateinit var binding: FragmentGroupsDetailMapBinding
    private lateinit var map: MapView

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

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
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
            onRoutesLoad(routes)
        }

        map.invalidate()
    }

    private fun onRoutesLoad(res: Result<List<Route>>) {
        handleResult(context, res) { routes ->
            val folderOverlay = FolderOverlay()
            routes.forEach { route ->
                val polyline = route.toPolyline()
                folderOverlay.add(polyline)
                polyline.setOnClickListener { _, _, _ ->
                    Toast.makeText(context, route.routeName, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener true
                }
            }
            map.overlays.add(folderOverlay)
            map.invalidate()
        }
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