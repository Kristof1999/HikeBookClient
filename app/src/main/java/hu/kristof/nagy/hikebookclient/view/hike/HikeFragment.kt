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
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
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
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikeViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import java.util.*

@AndroidEntryPoint
class HikeFragment : MapFragment() {
    private lateinit var binding: FragmentHikeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_hike, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

        val myLocationMarker = Marker(map)
        myLocationMarker.icon = AppCompatResources.getDrawable(
            requireContext(),
            org.osmdroid.bonuspack.R.drawable.person
        )
        map.overlays.add(myLocationMarker)

        val fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(requireContext())
        binding.hikeMyLocationFab.setOnClickListener {
            onMyLocation(fusedLocationProviderClient, myLocationMarker)
        }

        val viewModel: HikeViewModel by viewModels()
        val args: HikeFragmentArgs by navArgs()
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.route.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { userRoute ->
                myGeofence(fusedLocationProviderClient, myLocationMarker, userRoute, args, viewModel)
                mapCustomization(userRoute)
            }
        }
        handleOfflineLoad(requireContext()) {
            viewModel.loadUserRoute(args.routeName)
        }

        binding.hikeFinishButton.setOnLongClickListener {
            findNavController().navigate(
                R.id.action_hikeFragment_to_myMapFragment
            )
            return@setOnLongClickListener true
        }
        binding.hikeBackwardsPlanTransportButton.setOnLongClickListener {
            val isForward = false
            val directions = HikeFragmentDirections
                .actionHikeFragmentToHikePlanTransportFragment(isForward, args.routeName)
            findNavController().navigate(directions)
            return@setOnLongClickListener true
        }

        binding.hikeOfflineButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.offline_dialog_text)
                .show()
        }

        map.invalidate()
    }

    private fun initMap() {
        map = binding.hikeMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            addCopyRightOverlay()
        }
    }

    private fun myGeofence(
        fusedLocationProviderClient: FusedLocationProviderClient,
        myLocationMarker: Marker,
        route: UserRoute,
        args: HikeFragmentArgs,
        viewModel: HikeViewModel
    ) {
        var startTime = 0L
        binding.hikeStartButton.setOnClickListener {
            onMyLocation(fusedLocationProviderClient, myLocationMarker)
            val currentPosition = myLocationMarker.position
            val startPosition = route.points.first().toGeoPoint()

            if (isPointInCircle(
                    currentPosition,
                    startPosition,
                    Constants.GEOFENCE_RADIUS_IN_METERS
                )
            ) {
                Toast.makeText(requireContext(), "Start érintése sikeres!", Toast.LENGTH_LONG)
                    .show()
                // TODO: try to get better data
                // problem: circle is big -> time can be out dated -> avgSpeed is not realistic
                startTime = Calendar.getInstance().timeInMillis
            } else {
                Toast.makeText(requireContext(), "Nem vagy a start közelében.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.hikeFinishButton.setOnClickListener {
            handleOffline(requireContext()) {
                if (onFinish(fusedLocationProviderClient, myLocationMarker, route, startTime, viewModel)) {
                    findNavController().navigate(
                        R.id.action_hikeFragment_to_myMapFragment
                    )
                }
            }
        }

        binding.hikeBackwardsPlanTransportButton.setOnClickListener {
            handleOffline(requireContext()) {
                if (onFinish(fusedLocationProviderClient, myLocationMarker, route, startTime, viewModel)) {
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
        route: UserRoute,
        startTime: Long,
        viewModel: HikeViewModel
    ): Boolean {
        onMyLocation(fusedLocationProviderClient, myLocationMarker)
        val currentPosition = myLocationMarker.position
        val endPosition = route.points.last().toGeoPoint()

        if (isPointInCircle(
                currentPosition,
                endPosition,
                Constants.GEOFENCE_RADIUS_IN_METERS
            )
        ) {
            Toast.makeText(requireContext(), "Cél érintése sikeres!", Toast.LENGTH_LONG).show()
            val finishTime = Calendar.getInstance().timeInMillis
            viewModel.computeAndUpdateAvgSpeed(route, startTime, finishTime)
            return true
        } else {
            Toast.makeText(requireContext(), "Nem vagy a cél közelében.", Toast.LENGTH_LONG)
                .show()
            return false
        }
    }

    private fun isPointInCircle(point: GeoPoint, center: GeoPoint, radius: Int): Boolean {
        val polyline = Polyline()
        polyline.setPoints(listOf(center, point))
        val distance = polyline.distance

        return distance <= radius*radius
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
                    icon = MarkerUtils.getMarkerIcon(p.type, resources)
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
        myLocationMarker: Marker
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
                        val p = GeoPoint(location.latitude, location.longitude)
                        controller.setCenter(p)
                        myLocationMarker.position = p
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

    companion object {
        private const val TAG = "HikeFragment"
    }
}