// based on:
// https://developer.android.com/training/location/retrieve-current
// https://developer.android.com/training/location/permissions
// https://developer.android.com/training/permissions/requesting

package hu.kristof.nagy.hikebookclient.view.hike

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikeBinding
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikeViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import java.util.*

/**
 * A MapFragment to help the user with hiking.
 * It displays the route on a map, and uses GPS to locate the device.
 * It has several buttons:
 * one shows the device's last known location,
 * one shows a message which tells the user how can he/she hike while offline,
 * then there are 2 buttons to track progress,
 * and one button with which the user
 * can start to plan the the way home.
 */
@AndroidEntryPoint
class HikeFragment : MapFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHikeBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        initMap(binding)

        val myLocationMarker = makeMyLocationMarker()

        val fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(requireContext())
        binding.hikeMyLocationFab.setOnClickListener {
            onMyLocation(fusedLocationProviderClient, myLocationMarker)
        }

        val viewModel: HikeViewModel by viewModels()
        val args: HikeFragmentArgs by navArgs()

        viewModel.route.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { userRoute ->
                myGeofence(
                    fusedLocationProviderClient, myLocationMarker,
                    userRoute, args, viewModel, binding
                )
                mapCustomization(userRoute)
            }
        }
        handleOfflineLoad(requireContext()) {
            viewModel.loadUserRoute(args.routeName)
        }

        setupLongClickListeners(args, binding)

        binding.hikeOfflineButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.offline_dialog_text)
                .show()
        }

        map.invalidate()

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupLongClickListeners(
        args: HikeFragmentArgs,
        binding: FragmentHikeBinding
    ) {
        with(binding) {
            hikeFinishButton.setOnLongClickListener {
                findNavController().navigate(
                    R.id.action_hikeFragment_to_myMapFragment
                )
                return@setOnLongClickListener true
            }
            hikeBackwardsPlanTransportButton.setOnLongClickListener {
                val isForward = false
                val directions = HikeFragmentDirections
                    .actionHikeFragmentToHikePlanTransportFragment(isForward, args.routeName)
                findNavController().navigate(directions)
                return@setOnLongClickListener true
            }
        }
    }

    private fun makeMyLocationMarker() = Marker(map).apply {
        icon = AppCompatResources.getDrawable(
            requireContext(),
            org.osmdroid.bonuspack.R.drawable.person
        )
    }.also { myLocationMarker ->
        map.overlays.add(myLocationMarker)
    }

    private fun initMap(binding: FragmentHikeBinding) {
        map = binding.hikeMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
            addCopyRightOverlay()
        }
    }

    private fun myGeofence(
        fusedLocationProviderClient: FusedLocationProviderClient,
        myLocationMarker: Marker,
        route: Route.UserRoute,
        args: HikeFragmentArgs,
        viewModel: HikeViewModel,
        binding: FragmentHikeBinding
    ) {
        var startTime = 0L
        binding.hikeStartButton.setOnClickListener {
            onMyLocation(fusedLocationProviderClient, myLocationMarker) { myLocation ->
                val startPosition = route.points.first().toGeoPoint()
                if (viewModel.isPointInCircle(
                        myLocation,
                        startPosition,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                ) {
                    Toast.makeText(requireContext(), "Start érintése sikeres!", Toast.LENGTH_LONG)
                        .show()
                    val c = Calendar.getInstance()
                    startTime = c.timeInMillis
                    val hour = c.get(Calendar.HOUR_OF_DAY)
                    val minute = c.get(Calendar.MINUTE)
                    binding.hikeStartTimeTv.text = getString(R.string.start_time_text, hour, minute)
                } else {
                    Toast.makeText(requireContext(), "Nem vagy a start közelében.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        binding.hikeFinishButton.setOnClickListener {
            handleOffline(requireContext()) {
                onFinish(
                    fusedLocationProviderClient,
                    myLocationMarker,
                    route,
                    startTime,
                    viewModel
                ) {
                    findNavController().navigate(
                        R.id.action_hikeFragment_to_myMapFragment
                    )
                }
            }
        }

        binding.hikeBackwardsPlanTransportButton.setOnClickListener {
            handleOffline(requireContext()) {
                onFinish(
                    fusedLocationProviderClient,
                    myLocationMarker,
                    route,
                    startTime,
                    viewModel
                ) {
                    val isForward = false
                    val directions = HikeFragmentDirections
                        .actionHikeFragmentToHikePlanTransportFragment(isForward, args.routeName)
                    findNavController().navigate(directions)
                }
            }
        }
    }

    private fun onFinish(
        fusedLocationProviderClient: FusedLocationProviderClient,
        myLocationMarker: Marker,
        route: Route.UserRoute,
        startTime: Long,
        viewModel: HikeViewModel,
        f: () -> Unit
    ) {
        onMyLocation(fusedLocationProviderClient, myLocationMarker) { myLocation ->
            val endPosition = route.points.last().toGeoPoint()
            if (viewModel.isPointInCircle(
                    myLocation,
                    endPosition,
                    Constants.GEOFENCE_RADIUS_IN_METERS
                )
            ) {
                Toast.makeText(requireContext(), "Cél érintése sikeres!", Toast.LENGTH_LONG).show()
                if (startTime != 0L) {
                    val finishTime = Calendar.getInstance().timeInMillis
                    viewModel.computeAndUpdateAvgSpeed(startTime, finishTime)
                }
                f.invoke()
            } else {
                Toast.makeText(requireContext(), "Nem vagy a cél közelében.", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun mapCustomization(route: Route) {
        val polyline = route.toPolyline()
        map.overlays.add(polyline)

        FolderOverlay().also { folderOverlay ->
            for (p in route.points) {
                folderOverlay.add(Marker(map).apply {
                    setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
                    title = p.title
                    position = p.toGeoPoint()
                    icon = getMarkerIcon(p.type, resources)
                })
            }
            map.overlays.add(folderOverlay)
        }

        addCircleToMap(route.points.first().toGeoPoint())
        addCircleToMap(route.points.last().toGeoPoint())

        map.setMapCenterOnPolylineStart(polyline)
        map.controller.setZoom(18.0)
    }

    private fun addCircleToMap(center: GeoPoint) {
        val circle = Polygon(map)
        val points = Polygon.pointsAsCircle(center,
            Constants.GEOFENCE_RADIUS_IN_METERS.toDouble()
        )
        circle.points = points
        map.overlays.add(circle)
    }

    private val myLocationRequestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!isPermissionGranted(permissions, Manifest.permission.ACCESS_FINE_LOCATION) &&
            !isPermissionGranted(permissions, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.Builder(requireContext())
                    .setMessage("A saját pozíció funkció nem érhető el, ugyanis a működéshez hozzáférés szükséges az eszköz helyadataihoz.")
                    .show()
            }
    }

    private fun isPermissionGranted(
        permissions: Map<String, Boolean>,
        permission: String
    ): Boolean = permissions[permission] != null && permissions[permission] == true

    @SuppressLint("MissingPermission")
    private fun onMyLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        myLocationMarker: Marker,
        f: (myLocation: GeoPoint) -> Unit = {_ -> }
    ) {
        when {
            isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
            isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                fusedLocationProviderClient.lastLocation.run {
                    addOnSuccessListener { location ->
                        if (location == null) {
                            Toast.makeText(
                                requireContext(),
                                "Kérem, hogy kapcsolja be a GPS-t.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@addOnSuccessListener
                        }
                        val controller = map.controller
                        val myLocation = GeoPoint(location.latitude, location.longitude)
                        controller.setCenter(myLocation)
                        myLocationMarker.position = myLocation
                        f.invoke(myLocation)
                    }
                    addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Valamilyen hiba történt.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e(TAG, "Failed to get last known location: ${it.message}")
                    }
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                AlertDialog.Builder(requireContext())
                    .setMessage("A kevésbé jó minőségű helyhozzáféréssel a saját pozíció funkció megközelítő választ tud csak adni. Szeretné engedélyezni a helyhozzáférést?")
                    .setPositiveButton("Igen") { _, _ ->
                        myLocationRequestPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                    .setNegativeButton("Nem") { _, _ -> }
                    .show()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(requireContext())
                    .setMessage("A jó minőségű helyhozzáféréssel a saját pozíció funkció pontos választ tud adni. Szeretné engedélyezni a helyhozzáférést?")
                    .setPositiveButton("Igen") { _, _ ->
                        myLocationRequestPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    }
                    .setNegativeButton("Nem") { _, _ -> }
                    .show()
            }
            else -> {
                myLocationRequestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }
    }

    private fun isPermissionGranted(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.HIKE
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "HikeFragment"
    }
}