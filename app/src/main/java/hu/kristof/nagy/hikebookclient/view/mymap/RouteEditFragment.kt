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
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentRouteEditBinding
import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.viewModel.mymap.RouteEditViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
 * A Fragment to edit the chosen route.
 */
@AndroidEntryPoint
class RouteEditFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var map: MapView
    private lateinit var binding: FragmentRouteEditBinding
    private var isDeleteOn = false // TODO: handle interruptions: device rotation, ... -> viewmodel
    private val viewModel: RouteEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_edit, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: RouteEditFragmentArgs by navArgs()
        initMap(args)

        binding.routeEditSpinner.onItemSelectedListener = this
        SpinnerUtils.setSpinnerAdapter(requireContext(), binding.routeEditSpinner)

        val routeName = args.userRoute.routeName
        binding.routeEditRouteNameEditText.setText(routeName)
        val hikeDescription = args.userRoute.description
        binding.routeEditHikeDescriptionEditText.setText(hikeDescription)

        val viewModel: RouteEditViewModel by viewModels()
        setup(viewModel, args.userRoute.points)
        setClickListeners(viewModel, routeName)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routeEditRes.observe(viewLifecycleOwner) {
            onRouteEditResult(it)
        }

        setMapClickListeners(viewModel)
    }

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

    private fun setClickListeners(
        viewModel: RouteEditViewModel,
        routeName: String
    ) {
        binding.routeEditEditButton.setOnClickListener {
            onEdit(viewModel, routeName)
        }
        binding.routeEditDeleteSwitch.setOnCheckedChangeListener { _, isChecked ->
            isDeleteOn = isChecked
        }
    }

    private fun onEdit(
        viewModel: RouteEditViewModel,
        routeName: String
    ) {
        try {
            val newRouteName = binding.routeEditRouteNameEditText.text.toString()
            val hikeDescription = binding.routeEditHikeDescriptionEditText.text.toString()
            viewModel.onRouteEdit(routeName, newRouteName, hikeDescription)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMapClickListeners(viewModel: RouteEditViewModel) {
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

    private fun onRouteEditResult(res: Result<Boolean>) {
        handleResult(context, res) {
            findNavController().navigate(
                R.id.action_routeEditFragment_to_myMapFragment
            )
        }
    }

    private fun initMap(args: RouteEditFragmentArgs) {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.routeEditMap
        map.addCopyRightOverlay()

        val mapController = map.controller
        mapController.setZoom(Constants.START_ZOOM)
        mapController.setCenter(args.userRoute.toPolyline().bounds.centerWithDateLine)
    }

    // adapter design pattern/wrapper kellene ehelyett
    private fun setup(
        viewModel: RouteEditViewModel,
        points: List<Point>
    ) {
        val markerIcon = MarkerUtils.getMarkerIcon(viewModel.markerType, requireActivity())
        val markers = ArrayList<MyMarker>()
        val polylines = ArrayList<Polyline>()

        val firstMarker = Marker(map)
        val firstMarkerType = points.first().type
        val myFirstMarker = MyMarker(firstMarker, firstMarkerType, points.first().title)
        MarkerUtils.customizeMarker(myFirstMarker,
            MarkerUtils.getMarkerIcon(firstMarkerType, requireActivity()),
            GeoPoint(points.first().latitude, points.first().longitude)
        )
        map.overlays.add(firstMarker)
        markers.add(myFirstMarker)
        setMarkerListeners(firstMarker, viewModel)
        for (point in points.subList(1, points.size-1)) {
            val marker = Marker(map)
            val markerType = point.type
            val myMarker = MyMarker(marker, markerType, point.title)
            MarkerUtils.customizeMarker(myMarker,
                MarkerUtils.getMarkerIcon(markerType, requireActivity()),
                GeoPoint(point.latitude, point.longitude)
            )
            map.overlays.add(marker)
            markers.add(myMarker)
            setMarkerListeners(marker, viewModel)

            val polyline = MarkerUtils.makePolylineFromLastTwo(markers)
            map.overlays.add(polyline)
            polylines.add(polyline)
        }
        val lastMarker = Marker(map)
        val lastMarkerType = points.last().type
        val myLastMarker = MyMarker(lastMarker, lastMarkerType, points.last().title)
        MarkerUtils.customizeMarker(myLastMarker,
            MarkerUtils.getMarkerIcon(lastMarkerType, requireActivity()),
            GeoPoint(points.last().latitude, points.last().longitude)
        )
        map.overlays.add(lastMarker)
        markers.add(myLastMarker)
        setMarkerListeners(lastMarker, viewModel)
        val polyline = MarkerUtils.makePolylineFromLastTwo(markers)
        map.overlays.add(polyline)
        polylines.add(polyline)
        map.invalidate()
        viewModel.setup(markers, polylines)
    }

    private fun onSingleTap(
        p: GeoPoint?,
        viewModel: RouteEditViewModel
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
        viewModel: RouteEditViewModel
    ) {
        newMarker.setOnMarkerClickListener(Marker.OnMarkerClickListener { marker, mapView ->
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
        viewModel: RouteEditViewModel
    ) {
        if (viewModel.onDelete(marker, requireActivity().getDrawable(R.drawable.marker_image)!!)) {
            marker.remove(mapView)
            mapView.invalidate()
        } else {
            Toast.makeText(
                context, "Csak a legutóbb felvett pontot lehet törölni.", Toast.LENGTH_SHORT
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