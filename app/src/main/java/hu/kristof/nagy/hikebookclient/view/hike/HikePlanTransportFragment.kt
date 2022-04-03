package hu.kristof.nagy.hikebookclient.view.hike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikePlanTransportBinding
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.MarkerUtils
import hu.kristof.nagy.hikebookclient.util.SpinnerUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikePlanTransportViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

@AndroidEntryPoint
class HikePlanTransportFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentHikePlanTransportBinding
    private val viewModel: HikePlanTransportViewModel by viewModels()
    private lateinit var map: MapView

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
            val startPoint = args.userRoute.points.first()
            hikeStartMarker.position = startPoint.toGeoPoint()
            hikeStartMarker.title = startPoint.title
            hikeStartMarker.icon = AppCompatResources.getDrawable(
                requireContext(), org.osmdroid.library.R.drawable.marker_default
            )
            map.overlays.add(hikeStartMarker)
        } else {
            val hikeEndMarker = Marker(map)
            val startPoint = args.userRoute.points.last()
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
        val startMarker = Marker(map)
        startMarker.position = viewModel.startPoint
        startMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        startMarker.icon = MarkerUtils.getMarkerIcon(MarkerType.SET, requireContext())
        map.overlays.add(startMarker)
        viewModel.startPointChanged.observe(viewLifecycleOwner) {
            startMarker.position = viewModel.startPoint
            map.invalidate()
        }
        val endMarker = Marker(map)
        endMarker.position = viewModel.endPoint
        endMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        endMarker.icon = MarkerUtils.getMarkerIcon(MarkerType.NEW, requireContext())
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
                startPoint, endPoint, transportType, args.userRoute, args.isForward
            )
        findNavController().navigate(directions)
    }

    private fun initMap() {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        map = binding.hikePlanTransportMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        SpinnerUtils.onTransportItemSelected(pos, viewModel)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
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