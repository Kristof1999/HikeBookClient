// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki

package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentRouteCreateBinding
import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.viewModel.mymap.RouteCreateViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
 * A Fragment to create a route for the logged in user.
 */
@AndroidEntryPoint
class RouteCreateFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var map: MapView
    private lateinit var binding: FragmentRouteCreateBinding
    private var isDeleteOn = false
    private val viewModel: RouteCreateViewModel by viewModels()

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
        initMap()

        binding.routeCreateMarkerSpinner.onItemSelectedListener = this
        SpinnerUtils.setSpinnerAdapter(requireContext(), binding.routeCreateMarkerSpinner)

        setClickListeners(viewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routeCreateRes.observe(viewLifecycleOwner) {
            onRouteCreateResult(it)
            // névnek egyedinek kell lennie
        }

        setMapClickListeners(viewModel)
    }

    // TODO: try to use sg less error-prone/more flexible instead of ordinal and pos
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when (pos) {
            MarkerType.NEW.ordinal -> {
                viewModel.markerType = MarkerType.NEW
            }
            MarkerType.CASTLE.ordinal -> {
                viewModel.markerType = MarkerType.CASTLE
            }
            MarkerType.LOOKOUT.ordinal -> {
                viewModel.markerType = MarkerType.LOOKOUT
            }
            MarkerType.TEXT.ordinal -> {
                val dialogFragment = TextDialogFragment()
                dialogFragment.show(parentFragmentManager, "text")
                dialogFragment.text.observe(viewLifecycleOwner) {
                    viewModel.markerTitle = it
                }
                viewModel.markerType = MarkerType.TEXT
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }

    private fun setClickListeners(viewModel: RouteCreateViewModel) {
        binding.routeCreateDeleteSwitch.setOnCheckedChangeListener { _, isChecked ->
            isDeleteOn = isChecked
        }
        binding.routeCreateCreateButton.setOnClickListener {
            try {
                viewModel.onRouteCreate(
                    binding.routeCreateRouteNameEditText.text.toString(),
                    binding.routeCreateHikeDescriptionEditText.text.toString()
                )
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setMapClickListeners(viewModel: RouteCreateViewModel) {
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

    private fun onRouteCreateResult(res: Result<Boolean>) {
        handleResult(context, res) {
            findNavController().navigate(
                R.id.action_routeCreateFragment_to_myMapFragment
            )
        }
    }

    private fun initMap() {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.routeCreateMap
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()
    }

    private fun onSingleTap(
        p: GeoPoint?,
        viewModel: RouteCreateViewModel
    ): Boolean {
        InfoWindow.closeAllInfoWindowsOn(map)

        if (isDeleteOn)
            return true

        val newMarker = Marker(map)
        val markerIcon = MarkerUtils.getMarkerIcon(viewModel.markerType, requireActivity())
        val setMarkerIcon = requireActivity().getDrawable(R.drawable.set_marker_image)!!
        viewModel.onSingleTap(newMarker, p, markerIcon, setMarkerIcon, map.overlays)
        setMarkerListeners(newMarker, viewModel)
        map.invalidate()
        return true
    }

    private fun setMarkerListeners(
        newMarker: Marker,
        viewModel: RouteCreateViewModel,
    ) {
        newMarker.setOnMarkerClickListener(Marker.OnMarkerClickListener { marker, mapView ->
            // TODO: move logic to viewModel
            if (isDeleteOn) {
                onDelete(marker, mapView, viewModel)
            } else {
                if (marker.isInfoWindowShown) {
                    marker.closeInfoWindow()
                } else {
                    marker.showInfoWindow()
                }
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
        viewModel: RouteCreateViewModel
    ) {
        if (viewModel.onDelete(marker, requireActivity().getDrawable(R.drawable.marker_image)!!)) {
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