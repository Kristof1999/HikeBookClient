// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki

package hu.kristof.nagy.hikebookclient.view.mymap

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentRouteCreateBinding
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MapHelper
import hu.kristof.nagy.hikebookclient.viewModel.mymap.RouteCreateViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@AndroidEntryPoint
class RouteCreateFragment : Fragment() {
    private lateinit var map: MapView
    private lateinit var binding: FragmentRouteCreateBinding
    private var isDeleteOn = false // TODO: handle interruptions: device rotation, ...

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

        val viewModel: RouteCreateViewModel by viewModels()
        binding.routeCreateDeleteSwitch.setOnCheckedChangeListener { _, isChecked ->
            isDeleteOn = isChecked
        }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.routeCreateCreateButton.setOnClickListener {
            try {
                viewModel.onRouteCreate(binding.routeCreateRouteNameEditText.text.toString())
            } catch(e: IllegalArgumentException) {
                Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.routeCreateRes.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(
                    R.id.action_routeCreateFragment_to_myMapFragment
                )
            else
                Toast.makeText(
                    context, getText(R.string.generic_error_msg), Toast.LENGTH_SHORT
                ).show()
            // névnek egyedinek kell lennie
        }

        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                return onSingleTap(p, viewModel)
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                // itt (is) lehetne kiemelt pontok közül választani
                return true
            }
        })
        map.overlays.add(0, mapEventsOverlay)
        map.invalidate()
    }

    private fun onSingleTap(
        p: GeoPoint?,
        viewModel: RouteCreateViewModel
    ): Boolean {
        //TODO: súgóba: törlés közben nem vehetünk fel új pontokat
        if (isDeleteOn)
            return true

        val newMarker = Marker(map)
        val markerIcon = requireActivity().getDrawable(R.drawable.marker_image)!!
        val setMarkerIcon = requireActivity().getDrawable(R.drawable.set_marker_image)!!
        viewModel.onSingleTap(newMarker, p, markerIcon, setMarkerIcon, map.overlays)
        setListeners(newMarker, viewModel, markerIcon)
        map.invalidate()
        return true
    }

    private fun setListeners(
        newMarker: Marker,
        viewModel: RouteCreateViewModel,
        markerIcon: Drawable
    ) {
        newMarker.setOnMarkerClickListener(Marker.OnMarkerClickListener { marker, mapView ->
            if (isDeleteOn) {
                onDelete(marker, mapView, viewModel, markerIcon)
            }
            return@OnMarkerClickListener true
        })

        newMarker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker?) {

            }

            override fun onMarkerDragEnd(marker: Marker?) {
                if (marker == null)
                    return

                viewModel.onMarkerDragEnd(marker)
                map.invalidate()
            }

            override fun onMarkerDragStart(marker: Marker?) {
                if (marker == null)
                    return

                viewModel.onMarkerDragStart(marker)
                map.invalidate()
            }
        })
    }

    private fun onDelete(
        marker: Marker,
        mapView: MapView,
        viewModel: RouteCreateViewModel,
        markerIcon: Drawable
    ) {
        if (viewModel.onDelete(marker, markerIcon)) {
            marker.remove(mapView)
            mapView.invalidate()
        } else {
            Toast.makeText(
                context, getString(R.string.not_last_point_delete_error_msg), Toast.LENGTH_SHORT
            ).show()
        }
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