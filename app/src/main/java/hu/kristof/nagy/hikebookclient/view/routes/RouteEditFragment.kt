package hu.kristof.nagy.hikebookclient.view.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentRouteEditBinding
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.getMarkerIcon
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.routes.OnSingleTapHandlerTextMarkerTypeDecorator
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteEditViewModel
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

/**
 * A RouteFragment to edit the chosen route.
 * The user can change the name of the route and it's description in editTexts,
 * and change the points of the route on a map.
 * The user can choose between which type of point to place next with a spinner.
 * With a switch, the user can decide, if he/she wants to delete the last placed marker.
 * With the save button, the user can save the route.
 */
@AndroidEntryPoint
class RouteEditFragment : RouteFragment() {
    private lateinit var binding: FragmentRouteEditBinding
    override val viewModel: RouteEditViewModel by viewModels()
    override val switch: SwitchCompat by lazy {
        binding.routeEditDeleteSwitch
    }
    private val args: RouteEditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteEditBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                context = requireContext()
                this.viewModel = this@RouteEditFragment.viewModel
                executePendingBindings()
            }

        map = binding.routeEditMap
        super.onCreateView(inflater, container, savedInstanceState)

        setupObservers()

        setupSpinner()

        handleOfflineLoad(requireContext()) {
            viewModel.loadRoute(args)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObservers() {
        with(viewModel) {
            route.observe(viewLifecycleOwner) { res ->
                handleResult(context, res) { route ->
                    showRoutePointsAndPolylinesOnMap(viewModel, route.points)
                    adaptView(route)
                    setMapClickListeners(requireContext(), map, binding.routeEditDeleteSwitch, viewModel)
                }
            }
            routeEditRes.observe(viewLifecycleOwner) {
                onRouteEditResult(it, args)
            }
        }
        OnSingleTapHandlerTextMarkerTypeDecorator
            .setSpinnerToDefault.observe(viewLifecycleOwner) {
                binding.routeEditSpinner.setSelection(0)
            }
    }

    private fun setupSpinner() {
        binding.routeEditSpinner.onItemSelectedListener = this
        setMarkerSpinnerAdapter(requireContext(), binding.routeEditSpinner)
    }

    private fun adaptView(route: Route) {
        val routeName = route.routeName
        binding.routeEditRouteNameEditText.setText(routeName)
        val hikeDescription = route.description
        binding.routeEditHikeDescriptionEditText.setText(hikeDescription)
    }

    private fun onRouteEditResult(res: ResponseResult<Boolean>, args: RouteEditFragmentArgs) {
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

    private fun showRoutePointsAndPolylinesOnMap(
        viewModel: RouteEditViewModel,
        points: List<Point>
    ) {
        val markers = mutableListOf<MyMarker>()
        val polylines = mutableListOf<Polyline>()

        val makeMyMarker = { point: Point ->
            val marker = Marker(map)
            marker.customize(
                point.title,
                getMarkerIcon(point.type, resources),
                point.toGeoPoint()
            )
            marker.setListeners(
                requireContext(), map, switch, viewModel
            )
            val myMarker = MyMarker(marker, point.type, point.title)
            myMarker
        }

        makeMyMarker(points.first()).let { myFirstMarker ->
            markers.add(myFirstMarker)
            map.overlays.add(myFirstMarker.marker)
        }
        for (point in points.subList(1, points.size)) {
            makeMyMarker(point).let { myMarker ->
                markers.add(myMarker)
                map.overlays.add(myMarker.marker)
            }

            makePolylineFromLastTwo(markers).let { polyline ->
                polylines.add(polyline)
                map.overlays.add(polyline)
            }
        }

        map.invalidate()
        viewModel.setup(markers, polylines)
    }

    private fun makePolylineFromLastTwo(
        markers: List<MyMarker>
    ): Polyline = Polyline().apply {
        addPoint(markers[markers.size - 2].marker.position)
        addPoint(markers[markers.size - 1].marker.position)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.ROUTE_EDIT
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}