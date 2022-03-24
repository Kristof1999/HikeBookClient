package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
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
import hu.kristof.nagy.hikebookclient.model.HelpRequestType
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.viewModel.mymap.RouteEditViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

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
        setHasOptionsMenu(true)
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

        MapUtils.setMapClickListeners(requireContext(), map, isDeleteOn, viewModel)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        SpinnerUtils.onItemSelected(pos, viewModel, parentFragmentManager, viewLifecycleOwner)
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
        MarkerUtils.setMarkerListeners(requireContext(), map, isDeleteOn, firstMarker, viewModel)
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
            MarkerUtils.setMarkerListeners(requireContext(), map, isDeleteOn, marker, viewModel)

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
        MarkerUtils.setMarkerListeners(requireContext(), map, isDeleteOn, lastMarker, viewModel)
        val polyline = MarkerUtils.makePolylineFromLastTwo(markers)
        map.overlays.add(polyline)
        polylines.add(polyline)
        map.invalidate()
        viewModel.setup(markers, polylines)
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
        if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.ROUTE_EDIT
            val action = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(action)
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
}