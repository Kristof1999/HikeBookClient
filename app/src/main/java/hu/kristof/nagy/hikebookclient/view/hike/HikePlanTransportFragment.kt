package hu.kristof.nagy.hikebookclient.view.hike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikePlanTransportBinding
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikePlanTransportViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

@AndroidEntryPoint
class HikePlanTransportFragment : MapFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentHikePlanTransportBinding
    private val viewModel: HikePlanTransportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_hike_plan_transport, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

        val args: HikePlanTransportFragmentArgs by navArgs()
        if (args.isForward) {
            val hikeStartMarker = Marker(map)
            val startPoint = args.route.points.first()
            hikeStartMarker.position = startPoint.toGeoPoint()
            hikeStartMarker.title = startPoint.title
            hikeStartMarker.icon = AppCompatResources.getDrawable(
                requireContext(), org.osmdroid.library.R.drawable.marker_default
            )
            map.overlays.add(hikeStartMarker)
        } else {
            val hikeEndMarker = Marker(map)
            val startPoint = args.route.points.last()
            hikeEndMarker.position = startPoint.toGeoPoint()
            hikeEndMarker.title = startPoint.title
            hikeEndMarker.icon = AppCompatResources.getDrawable(
                requireContext(), org.osmdroid.library.R.drawable.marker_default
            )
            map.overlays.add(hikeEndMarker)
        }


        binding.hikePlanTransportTransportMeanSpinner.onItemSelectedListener = this
        SpinnerUtils.setTransportSpinnerAdapter(requireContext(), binding.hikePlanTransportTransportMeanSpinner)

        binding.hikePlanTransportStartButton.setOnClickListener {
            onTransportStart(args)
        }

        handleStartAndEndSwitches()

        handleStartAndEndPointChanges()

        addMapEventsOverlay()

        map.invalidate()
    }

    private fun addMapEventsOverlay() {
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                InfoWindow.closeAllInfoWindowsOn(map)
                viewModel.onSingleTap(p!!)
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return true
            }
        })
        map.overlays.add(0, mapEventsOverlay)
    }

    private fun handleStartAndEndPointChanges() {
        val startMarker = Marker(map).apply {
            position = viewModel.startPoint
            setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
            icon = MarkerUtils.getMarkerIcon(MarkerType.SET, resources)
        }
        map.overlays.add(startMarker)
        viewModel.startPointChanged.observe(viewLifecycleOwner) {
            startMarker.position = viewModel.startPoint
            map.invalidate()
        }
        val endMarker = Marker(map).apply {
            position = viewModel.endPoint
            setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
            icon = MarkerUtils.getMarkerIcon(MarkerType.NEW, resources)
        }
        map.overlays.add(endMarker)
        viewModel.endPointChanged.observe(viewLifecycleOwner) {
            endMarker.position = viewModel.endPoint
            map.invalidate()
        }
    }

    private fun handleStartAndEndSwitches() {
        binding.hikePlanTransportStartSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setStartTo(isChecked)
        }
        binding.hikePlanTransportEndSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEndTo(isChecked)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.switchOffStart.observe(viewLifecycleOwner) {
            binding.hikePlanTransportStartSwitch.isChecked = false
        }
        viewModel.switchOffEnd.observe(viewLifecycleOwner) {
            binding.hikePlanTransportEndSwitch.isChecked = false
        }
    }

    private fun onTransportStart(args: HikePlanTransportFragmentArgs) {
        val startPoint = Point(
            viewModel.startPoint.latitude, viewModel.startPoint.longitude,
            MarkerType.SET, ""
        )
        val endPoint = Point(
            viewModel.endPoint.latitude, viewModel.endPoint.longitude,
            MarkerType.NEW, ""
        )
        // TODO: implement public transport
        // starting point: https://menetrend.app/data-sources
        val transportType = viewModel.transportType
        val directions = HikePlanTransportFragmentDirections
            .actionHikePlanFragmentToHikeTransportFragment(
                startPoint, endPoint, transportType, args.route, args.isForward
            )
        findNavController().navigate(directions)
    }

    private fun initMap() {
        map = binding.hikePlanTransportMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
            addCopyRightOverlay()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when(pos) {
            TransportType.BICYCLE.ordinal -> {
                viewModel.transportType = TransportType.BICYCLE
            }
            TransportType.CAR.ordinal -> {
                viewModel.transportType = TransportType.CAR
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }
}