package hu.kristof.nagy.hikebookclient.view.routes

import android.os.Bundle
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
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentRouteEditBinding
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteEditViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
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
    private val viewModel: RouteEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        SpinnerUtils.setMarkerSpinnerAdapter(requireContext(), binding.routeEditSpinner)

        val routeName = args.userRoute.routeName
        binding.routeEditRouteNameEditText.setText(routeName)
        val hikeDescription = args.userRoute.description
        binding.routeEditHikeDescriptionEditText.setText(hikeDescription)

        val viewModel: RouteEditViewModel by viewModels()
        setup(viewModel, args.userRoute.points)
        binding.routeEditEditButton.setOnClickListener {
            onEdit(viewModel, args.userRoute)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routeEditRes.observe(viewLifecycleOwner) {
            onRouteEditResult(it)
        }

        MapUtils.setMapClickListeners(requireContext(), map, binding.routeEditDeleteSwitch, viewModel)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        SpinnerUtils.onMarkerItemSelected(pos, viewModel, parentFragmentManager, viewLifecycleOwner)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }

    private fun onEdit(
        viewModel: RouteEditViewModel,
        oldUserRoute: Route
    ) {
        try {
            val newRouteName = binding.routeEditRouteNameEditText.text.toString()
            val newHikeDescription = binding.routeEditHikeDescriptionEditText.text.toString()
            viewModel.onRouteEdit(oldUserRoute, newRouteName, newHikeDescription)
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
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        map = binding.routeEditMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.addCopyRightOverlay()

        val polyline = args.userRoute.toPolyline()
        map.setMapCenterOnPolylineCenter(polyline)
        map.setZoomForPolyline(polyline)
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
        MarkerUtils.setMarkerListeners(
            requireContext(), map, binding.routeEditDeleteSwitch, firstMarker, viewModel
        )
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
            MarkerUtils.setMarkerListeners(
                requireContext(), map, binding.routeEditDeleteSwitch, marker, viewModel
            )

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
        MarkerUtils.setMarkerListeners(
            requireContext(), map, binding.routeEditDeleteSwitch, lastMarker, viewModel
        )
        val polyline = MarkerUtils.makePolylineFromLastTwo(markers)
        map.overlays.add(polyline)
        polylines.add(polyline)
        map.invalidate()
        viewModel.setup(markers, polylines)
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
            val requestType = HelpRequestType.ROUTE_EDIT
            val action = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(action)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}