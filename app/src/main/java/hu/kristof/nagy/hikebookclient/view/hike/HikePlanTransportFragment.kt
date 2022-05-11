package hu.kristof.nagy.hikebookclient.view.hike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikePlanTransportBinding
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.view.routes.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikePlanTransportViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
 * A MapFragment to plan transportation between 2 points with a given transport mean.
 * It displays a map with 3 markers:
 * one marker is fixed, and shows the start/end of the hike,
 * and the other 2 markers show the start and end points of the planned route.
 * It has 2 switches, with which the user can set which marker he/she would like to move.
 * It has a spinner, from which the user can choose
 * with which transport mean he/she would like to travel.
 * It has a button to start travelling.
 */
@AndroidEntryPoint
class HikePlanTransportFragment : MapFragment(), AdapterView.OnItemSelectedListener {
    private val hikePlanTransportViewModel: HikePlanTransportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHikePlanTransportBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        val args: HikePlanTransportFragmentArgs by navArgs()

        setupObservers(args, binding)

        setupSwitchClickListeners(binding)

        initMap(binding)

        setupLoad(args)

        binding.hikePlanTransportTransportMeanSpinner.onItemSelectedListener = this
        setSpinnerAdapter(
            requireContext(),
            binding.hikePlanTransportTransportMeanSpinner,
            R.array.transport_types
        )

        binding.hikePlanTransportStartButton.setOnClickListener {
            onTransportStart(args)
        }

        handleStartAndEndPointChanges()

        addMapEventsOverlay()

        map.invalidate()

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupSwitchClickListeners(binding: FragmentHikePlanTransportBinding) {
        with (binding) {
            hikePlanTransportStartSwitch.setOnClickListener {
                val checked = hikePlanTransportStartSwitch.isChecked
                hikePlanTransportViewModel.setStartTo(checked)
            }
            hikePlanTransportEndSwitch.setOnClickListener {
                val checked = hikePlanTransportEndSwitch.isChecked
                hikePlanTransportViewModel.setEndTo(checked)
            }
        }
    }

    private fun setupObservers(
        args: HikePlanTransportFragmentArgs,
        binding: FragmentHikePlanTransportBinding
    ) {
        hikePlanTransportViewModel.apply {
            route.observe(viewLifecycleOwner) { res ->
                handleResult(context, res) { route ->
                    adaptView(args, route)
                }
            }
            setStartNext.observe(viewLifecycleOwner) {
                binding.hikePlanTransportStartSwitch.isChecked = it
            }
            setEndNext.observe(viewLifecycleOwner) {
                binding.hikePlanTransportEndSwitch.isChecked = it
            }
        }
    }

    private fun setupLoad(args: HikePlanTransportFragmentArgs) {
        handleOfflineLoad(requireContext()) {
            hikePlanTransportViewModel.loadRoute(args.routeName)
        }
    }

    private fun adaptView(args: HikePlanTransportFragmentArgs, route: Route) {
        val myLocationIcon = AppCompatResources.getDrawable(
            requireContext(), org.osmdroid.library.R.drawable.marker_default
        )
        if (args.isForward) {
            Marker(map).apply {
                val hikeStartPoint = route.points.first()
                position = hikeStartPoint.toGeoPoint()
                title = hikeStartPoint.title
                icon = myLocationIcon
            }.let { hikeStartMarker ->
                map.overlays.add(hikeStartMarker)
            }
        } else {
            Marker(map).apply {
                val hikeEndPoint = route.points.last()
                position = hikeEndPoint.toGeoPoint()
                title = hikeEndPoint.title
                icon = myLocationIcon
            }.let { hikeEndMarker ->
                map.overlays.add(hikeEndMarker)
            }
        }
        map.invalidate()
    }

    private fun addMapEventsOverlay() {
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                InfoWindow.closeAllInfoWindowsOn(map)
                hikePlanTransportViewModel.onSingleTap(p!!)
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return true
            }
        })
        map.overlays.add(0, mapEventsOverlay)
    }

    private fun handleStartAndEndPointChanges() {
        Marker(map).apply {
            position = hikePlanTransportViewModel.startPoint
            setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
            icon = getMarkerIcon(MarkerType.SET, resources)

            hikePlanTransportViewModel.startPointChanged.observe(viewLifecycleOwner) {
                position = hikePlanTransportViewModel.startPoint
                map.invalidate()
            }
        }.let { startMarker ->
            map.overlays.add(startMarker)
        }

        Marker(map).apply {
            position = hikePlanTransportViewModel.endPoint
            setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
            icon = getMarkerIcon(MarkerType.NEW, resources)

            hikePlanTransportViewModel.endPointChanged.observe(viewLifecycleOwner) {
                position = hikePlanTransportViewModel.endPoint
                map.invalidate()
            }
        }.let { endMarker ->
            map.overlays.add(endMarker)
        }
    }

    private fun onTransportStart(args: HikePlanTransportFragmentArgs) {
        val startPoint = Point(
            hikePlanTransportViewModel.startPoint.latitude, hikePlanTransportViewModel.startPoint.longitude,
            MarkerType.SET, ""
        )
        val endPoint = Point(
            hikePlanTransportViewModel.endPoint.latitude, hikePlanTransportViewModel.endPoint.longitude,
            MarkerType.NEW, ""
        )
        val transportType = hikePlanTransportViewModel.transportType
        val directions = HikePlanTransportFragmentDirections
            .actionHikePlanFragmentToHikeTransportFragment(
                startPoint, endPoint, transportType, args.isForward, args.routeName
            )
        findNavController().navigate(directions)
    }

    private fun initMap(binding: FragmentHikePlanTransportBinding) {
        map = binding.hikePlanTransportMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
            addCopyRightOverlay()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when(pos) {
            TransportType.BICYCLE.ordinal -> {
                hikePlanTransportViewModel.transportType = TransportType.BICYCLE
            }
            TransportType.CAR.ordinal -> {
                hikePlanTransportViewModel.transportType = TransportType.CAR
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.HIKE_PLAN_TRANSPORT
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}