package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentRouteCreateBinding
import hu.kristof.nagy.hikebookclient.MapHelper
import hu.kristof.nagy.hikebookclient.model.Constants
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class RouteCreateFragment : Fragment() {
    private lateinit var map: MapView
    private lateinit var binding: FragmentRouteCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_create, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.routeCreateMap

        val mapController = map.controller
        mapController.setZoom(Constants.START_ZOOM)
        mapController.setCenter(Constants.START_POINT)
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                val marker = Marker(map)
                marker.position = p
                marker.icon = requireActivity().getDrawable(R.drawable.marker_image)
                map.overlays.add(marker)
                map.invalidate()
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                TODO("Not yet implemented")
            }
        })
        map.overlays.add(mapEventsOverlay)
        map.invalidate()
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