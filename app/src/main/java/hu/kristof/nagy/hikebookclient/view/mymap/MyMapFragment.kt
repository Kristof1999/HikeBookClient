// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki

package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentMyMapBinding
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel
import org.osmdroid.config.Configuration.getInstance
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
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_map, container, false
        )
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

    private fun onRoutesLoad(routes: List<Route>) {
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
        getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.myMap
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
}