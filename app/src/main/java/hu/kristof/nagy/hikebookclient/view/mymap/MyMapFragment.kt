// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki

package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentMyMapBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.MapFragment
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

/**
 * A Fragment to display the routes of the logged in user on a map.
 */
@AndroidEntryPoint
class MyMapFragment : MapFragment() {
    private lateinit var binding: FragmentMyMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_map, container, false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

        setClickListeners()

        val viewModel: MyMapViewModel by activityViewModels()
        viewModel.loadRoutesForLoggedInUser()
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routes.observe(viewLifecycleOwner) { routes ->
            MapUtils.onRoutesLoad(routes, context, map)
        }
    }

    private fun setClickListeners() {
        binding.switchToMyMapListButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_myMapFragment_to_myMapListFragment
            )
        }
        binding.routeCreateFab.setOnClickListener {
            val routeType = RouteType.USER
            val directions = MyMapFragmentDirections
                .actionMyMapFragmentToRouteCreateFragment(routeType, null, null)
            findNavController().navigate(directions)
        }
    }

    private fun initMap() {
        map = binding.myMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.MY_MAP
            val action = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(action)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}