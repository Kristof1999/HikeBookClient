package hu.kristof.nagy.hikebookclient.view.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentRouteEditBinding
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteEditViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

/**
 * A Fragment to edit the chosen route.
 */
@AndroidEntryPoint
class RouteEditFragment : MapFragment(), AdapterView.OnItemSelectedListener {
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

        initMap()

        binding.routeEditSpinner.onItemSelectedListener = this
        SpinnerUtils.setMarkerSpinnerAdapter(requireContext(), binding.routeEditSpinner)

        val args: RouteEditFragmentArgs by navArgs()
        val viewModel: RouteEditViewModel by viewModels()
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.loadRoute(args)
        viewModel.route.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { route ->
                showRoutePointsOnMap(viewModel, route.points)
                adaptView(route)
                showRoutePolylineOnMap(route)
            }
        }

        binding.routeEditEditButton.setOnClickListener {
            onRouteEdit(viewModel)
        }

        viewModel.routeEditRes.observe(viewLifecycleOwner) {
            onRouteEditResult(it, args)
        }

        MapUtils.setMapClickListeners(requireContext(), map, binding.routeEditDeleteSwitch, viewModel)
    }

    private fun adaptView(route: Route) {
        val routeName = route.routeName
        binding.routeEditRouteNameEditText.setText(routeName)
        val hikeDescription = route.description
        binding.routeEditHikeDescriptionEditText.setText(hikeDescription)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        SpinnerUtils.onMarkerItemSelected(pos, viewModel, parentFragmentManager, viewLifecycleOwner)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }

    private fun onRouteEdit(
        viewModel: RouteEditViewModel
    ) {
        try {
            val newRouteName = binding.routeEditRouteNameEditText.text.toString()
            val newHikeDescription = binding.routeEditHikeDescriptionEditText.text.toString()
            viewModel.onRouteEdit(newRouteName, newHikeDescription)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun onRouteEditResult(res: Result<Boolean>, args: RouteEditFragmentArgs) {
        handleResult(context, res) {
            when (args.routeType) {
                RouteType.USER -> findNavController().navigate(
                    R.id.action_routeEditFragment_to_myMapFragment
                )
                RouteType.GROUP -> {
                    val groupName = args.groupName!!
                    // isConnectedPage is true because
                    // only a connected member can edit routes
                    val directions = RouteEditFragmentDirections
                        .actionRouteEditFragmentToGroupsDetailFragment(groupName, true)
                    findNavController().navigate(directions)
                }
            }
        }
    }

    private fun initMap() {
        map = binding.routeEditMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            addCopyRightOverlay()
            setStartZoomAndCenter()
        }
    }

    private fun showRoutePolylineOnMap(route: Route) {
        val polyline = route.toPolyline()
        map.setMapCenterOnPolylineCenter(polyline)
        map.setZoomForPolyline(polyline)
    }

    private fun showRoutePointsOnMap(
        viewModel: RouteEditViewModel,
        points: List<Point>
    ) {
        val markers = ArrayList<MyMarker>()
        val polylines = ArrayList<Polyline>()

        val makeMyMarker = { point: Point ->
            val marker = Marker(map)
            val markerType = point.type
            val myMarker = MyMarker(marker, markerType, point.title)
            marker.customize(
                myMarker.title,
                MarkerUtils.getMarkerIcon(markerType, resources),
                point.toGeoPoint()
            )
            marker.setListeners(
                requireContext(), map, binding.routeEditDeleteSwitch, viewModel
            )
            myMarker
        }

        makeMyMarker(points.first()).also { myFirstMarker ->
            map.overlays.add(myFirstMarker.marker)
            markers.add(myFirstMarker)
        }
        for (point in points.subList(1, points.size)) {
            makeMyMarker(point).also { myMarker ->
                map.overlays.add(myMarker.marker)
                markers.add(myMarker)
            }

            MarkerUtils.makePolylineFromLastTwo(markers).also { polyline ->
                map.overlays.add(polyline)
                polylines.add(polyline)
            }
        }

        map.invalidate()
        viewModel.setup(markers, polylines)
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