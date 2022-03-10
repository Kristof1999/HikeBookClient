// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki

package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentMyMapBinding
import hu.kristof.nagy.hikebookclient.MapHelper
import hu.kristof.nagy.hikebookclient.model.Constants
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.views.MapView

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

        getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.myMap

        val mapController = map.controller
        mapController.setZoom(Constants.START_ZOOM)
        mapController.setCenter(Constants.START_POINT)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MapHelper.onRequestPermissionsResult(
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