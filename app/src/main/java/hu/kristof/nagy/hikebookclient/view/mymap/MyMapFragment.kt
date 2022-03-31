// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki

package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentMyMapBinding
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay

/**
 * A Fragment to display the routes of the logged in user on a map.
 */
@AndroidEntryPoint
class MyMapFragment : Fragment() {
    private lateinit var map: MapView
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
            onRoutesLoad(routes)
        }
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

    private fun setClickListeners() {
        binding.switchToMyMapListButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_myMapFragment_to_myMapListFragment
            )
        }
        binding.routeCreateFab.setOnClickListener {
            findNavController().navigate(
                R.id.action_myMapFragment_to_routeCreateFragment
            )
        }
    }

    private fun initMap() {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        map = binding.myMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MapUtils.onRequestPermissionsResult(
            requestCode, permissions, grantResults, requireActivity()
        )
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
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