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
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

/**
 * A MapFragment to display the routes of the logged in user on a map.
 * It has a button with which the user can
 * create a route.
 * With another button, the user can switch to a list view of his/her routes.
 */
@AndroidEntryPoint
class MyMapFragment : MapFragment() {
    private lateinit var binding: FragmentMyMapBinding
    private val viewModel: MyMapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentMyMapBinding>(
            inflater, R.layout.fragment_my_map, container, false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        setupObserver()

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObserver() {
        viewModel.routes.observe(viewLifecycleOwner) { routes ->
            map.onRoutesLoad(routes as ResponseResult<List<Route>>, context)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

        setClickListeners()
    }

    private fun setClickListeners() = with(binding) {
        switchToMyMapListButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_myMapFragment_to_myMapListFragment
            )
        }
        routeCreateFab.setOnClickListener {
            val routeType = RouteType.USER
            val directions = MyMapFragmentDirections
                .actionMyMapFragmentToRouteCreateFragment(routeType, null)
            findNavController().navigate(directions)
        }
    }

    private fun initMap() {
        map = binding.myMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
            addCopyRightOverlay()
        }
    }

    override fun onResume() {
        super.onResume()
        handleOfflineLoad(requireContext()) {
            viewModel.loadRoutesForLoggedInUser()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.MY_MAP
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}