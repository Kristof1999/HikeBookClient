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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentRouteEditBinding
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MapHelper
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel
import hu.kristof.nagy.hikebookclient.viewModel.mymap.RouteEditViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@AndroidEntryPoint
class RouteEditFragment : Fragment() {
    private lateinit var map: MapView
    private lateinit var binding: FragmentRouteEditBinding
    private val args: RouteEditFragmentArgs by navArgs()
    private var isDeleteOn = false // TODO: handle interruptions: device rotation, ...

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
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.routeEditMap

        val mapController = map.controller
        // TODO: set center on center of route with appropriate zoom
        mapController.setZoom(Constants.START_ZOOM)
        mapController.setCenter(Constants.START_POINT)

        val sharedViewModel: MyMapViewModel by activityViewModels()
        val routeName = args.routeName
        binding.routeEditRouteNameEditText.setText(routeName)
        val route = sharedViewModel.routes.value!!.filter { route ->
            route.routeName == routeName
        }[0]

        val viewModel: RouteEditViewModel by viewModels()
        setup(viewModel, route.points)
        binding.routeEditEditButton.setOnClickListener {
            try {
                val routeName = binding.routeEditRouteNameEditText.text.toString()
                viewModel.onRouteEdit(route.routeName, routeName)
            } catch(e: IllegalArgumentException) {
                Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
            }
        }
        binding.routeEditDeleteSwitch.setOnCheckedChangeListener { _, isChecked ->
            isDeleteOn = isChecked
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routeEditRes.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(
                    R.id.action_routeEditFragment_to_myMapFragment
                )
            else
                Toast.makeText(
                    context, getString(R.string.generic_error_msg), Toast.LENGTH_SHORT
                ).show()
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

    // adapter design pattern/wrapper kellene ehelyett
    private fun setup(
        viewModel: RouteEditViewModel,
        points: List<Point>
    ) {
        val markerIcon = requireActivity().getDrawable(R.drawable.marker_image)!!
        val setMarkerIcon = requireActivity().getDrawable(R.drawable.set_marker_image)!!
        val markers = ArrayList<Marker>()
        val polylines = ArrayList<Polyline>()

        val firstMarker = Marker(map)
        firstMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        firstMarker.isDraggable = true
        firstMarker.position = GeoPoint(points.first().latitude, points.first().longitude)
        firstMarker.icon = setMarkerIcon
        map.overlays.add(firstMarker)
        markers.add(firstMarker)
        setListeners(firstMarker, viewModel, markerIcon)
        for (point in points.subList(1, points.size-1)) {
            val marker = Marker(map)
            marker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
            marker.isDraggable = true
            marker.position = GeoPoint(point.latitude, point.longitude)
            marker.icon = setMarkerIcon
            map.overlays.add(marker)
            markers.add(marker)
            setListeners(marker, viewModel, markerIcon)

            val polylinePoints = ArrayList<GeoPoint>()
            polylinePoints.add(markers[markers.size - 2].position)
            polylinePoints.add(markers[markers.size - 1].position)
            val polyline = Polyline()
            polyline.setPoints(polylinePoints)
            map.overlays.add(polyline)
            polylines.add(polyline)
        }
        val lastMarker = Marker(map)
        lastMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        lastMarker.isDraggable = true
        lastMarker.position = GeoPoint(points.last().latitude, points.last().longitude)
        lastMarker.icon = markerIcon
        map.overlays.add(lastMarker)
        markers.add(lastMarker)
        setListeners(lastMarker, viewModel, markerIcon)
        val polylinePoints = ArrayList<GeoPoint>()
        polylinePoints.add(markers[markers.size - 2].position)
        polylinePoints.add(markers[markers.size - 1].position)
        val polyline = Polyline()
        polyline.setPoints(polylinePoints)
        map.overlays.add(polyline)
        polylines.add(polyline)
        map.invalidate()
        viewModel.setup(markers, polylines)
    }

    private fun onSingleTap(
        p: GeoPoint?,
        viewModel: RouteEditViewModel
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
        viewModel: RouteEditViewModel,
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
        viewModel: RouteEditViewModel,
        markerIcon: Drawable
    ) {
        if (viewModel.onDelete(marker, markerIcon)) {
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