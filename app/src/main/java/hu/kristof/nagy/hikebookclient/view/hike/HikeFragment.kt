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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikeBinding
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MarkerUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setMapCenterOnPolylineStart
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline

@AndroidEntryPoint
class HikeFragment : Fragment() {
    private lateinit var map: MapView
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

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        map = binding.hikeMap
        map.setTileSource(TileSourceFactory.MAPNIK)

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

        val args: HikeFragmentArgs by navArgs()
        binding.hikeStartButton.setOnClickListener {
            onMyLocation(fusedLocationProviderClient, myLocationMarker)
            val currentPosition = myLocationMarker.position
            val startPosition = args.userRoute.points.first().toGeoPoint()

            if (isPointInCircle(currentPosition, startPosition, Constants.GEOFENCE_RADIUS_IN_METERS)) {
                Toast.makeText(requireContext(), "Start érintése sikeres!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Nem vagy a start közelében.", Toast.LENGTH_LONG).show()
            }
        }
        binding.hikeFinishButton.setOnClickListener {
            onMyLocation(fusedLocationProviderClient, myLocationMarker)
            val currentPosition = myLocationMarker.position
            val endPosition = args.userRoute.points.last().toGeoPoint()

            if (isPointInCircle(currentPosition, endPosition, Constants.GEOFENCE_RADIUS_IN_METERS)) {
                Toast.makeText(requireContext(), "Cél érintése sikeres!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Nem vagy a cél közelében.", Toast.LENGTH_LONG).show()
            }
        }
        binding.hikeHikeCloseFab.setOnClickListener {
            findNavController().navigate(
                R.id.action_hikeFragment_to_myMapFragment
            )
        }

        binding.hikeOfflineButton.setOnClickListener {
            // TODO: test if the cache still stays after we turn of the app (and wifi of course)
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.offline_dialog_text)
                .show()
        }

        mapCustomization(args)

        map.invalidate()
    }

    private fun isPointInCircle(point: GeoPoint, center: GeoPoint, radius: Int): Boolean {
        val polyline = Polyline()
        polyline.setPoints(listOf(center, point))
        val distance = polyline.distance

        return distance <= radius*radius
    }

    private fun mapCustomization(args: HikeFragmentArgs) {
        val polyLine = args.userRoute.toPolyline()
        map.overlays.add(polyLine)

        val folderOverlay = FolderOverlay()
        for (p in args.userRoute.points) {
            val marker = Marker(map)
            marker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
            marker.title = p.title
            marker.position = p.toGeoPoint()
            marker.icon = MarkerUtils.getMarkerIcon(p.type, requireContext())
            folderOverlay.add(marker)
        }
        map.overlays.add(folderOverlay)

        addCircleToMap(args.userRoute.points.first().toGeoPoint())
        addCircleToMap(args.userRoute.points.last().toGeoPoint())

        map.setMapCenterOnPolylineStart(args.userRoute.toPolyline())
        val controller = map.controller
        controller.setZoom(18.0)
        map.addCopyRightOverlay()
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
                                "Kérem, hogy kapcsolja be a GPS-t a saját pozíció megtekintéséhez.",
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
                    // TODO: update text -> next to my location, we use it for the geofence replacement
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

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    companion object {
        private const val TAG = "HikeFragment"
    }
}