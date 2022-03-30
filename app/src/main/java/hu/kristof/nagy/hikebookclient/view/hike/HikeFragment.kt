// based on:
// https://developer.android.com/training/location/retrieve-current
// https://developer.android.com/training/location/geofencing

package hu.kristof.nagy.hikebookclient.view.hike

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikeBinding
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

        val myLocationMarker = Marker(map)
        myLocationMarker.icon = AppCompatResources.getDrawable(requireContext(),
            org.osmdroid.bonuspack.R.drawable.person
        )
        map.overlays.add(myLocationMarker)

        val fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(requireContext())
        binding.hikeMyLocationFab.setOnClickListener {
            onMyLocation(fusedLocationProviderClient, myLocationMarker)
        }

        val args: HikeFragmentArgs by navArgs()
        val geofencingClient = LocationServices
            .getGeofencingClient(requireContext())
        initGeofence(args.userRoute.points, geofencingClient)

        // TODO: when finishing, compute average speed
        // if we entered start geofence
        handleFinishButton(geofencingClient)

        val polyLine = args.userRoute.toPolyline()
        map.overlays.add(polyLine)

        addCircleToMap(args.userRoute.points.first().toGeoPoint())
        addCircleToMap(args.userRoute.points.last().toGeoPoint())

        map.invalidate()
    }

    private fun addCircleToMap(center: GeoPoint) {
        val circle = Polygon(map)
        val points = Polygon.pointsAsCircle(center,
            Constants.GEOFENCE_RADIUS_IN_METERS.toDouble()
        )
        circle.points = points
        map.overlays.add(circle)
    }

    private fun initGeofence(points: List<Point>, geofencingClient: GeofencingClient) {
        val firstPointGeofence = Geofence.Builder()
            .setRequestId(Constants.GEOFENCE_REQUEST_ID_FIRST_POINT)
            .setCircularRegion(
                points.first().latitude,
                points.first().longitude,
                Constants.GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
        val lastPointGeofence = Geofence.Builder()
                .setRequestId(Constants.GEOFENCE_REQUEST_ID_LAST_POINT)
                .setCircularRegion(
                    points.last().latitude,
                    points.last().longitude,
                    Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()
        // TODO: decide if one geofenceRequest would be enough
        val geofencingRequestFirstPoint = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
            addGeofences(listOf(firstPointGeofence))
        }.build()
        val geofencingRequestLastPoint = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
            addGeofences(listOf(lastPointGeofence))
        }.build()
        val geofenceFirstPointPendingIntent: PendingIntent by lazy {
            val intent = Intent(requireContext(), GeofenceFirstPointBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(
                requireContext(), Constants.GEOFENCE_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val geofenceLastPointPendingIntent: PendingIntent by lazy {
            val intent = Intent(requireContext(), GeofenceLastPointBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(
                requireContext(), Constants.GEOFENCE_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        addGeofence(geofencingClient,
            geofencingRequestFirstPoint,
            geofenceFirstPointPendingIntent
        )
        addGeofence(geofencingClient,
            geofencingRequestLastPoint,
            geofenceLastPointPendingIntent
        )
    }

    private fun handleFinishButton(geofencingClient: GeofencingClient) {
        binding.hikeHikeFinishFab.setOnClickListener {
            if (GeofenceLastPointBroadcastReceiver.entered) {
                removeGeofences(geofencingClient)
                findNavController().navigate(
                    R.id.action_hikeFragment_to_myMapFragment
                )
            } else {
                Toast.makeText(requireContext(), "Nem vagy elég közel a célhoz.", Toast.LENGTH_LONG)
                    .show()
            }
        }
        binding.hikeHikeFinishFab.setOnLongClickListener {
            removeGeofences(geofencingClient)
            findNavController().navigate(
                R.id.action_hikeFragment_to_myMapFragment
            )
            return@setOnLongClickListener true
        }
    }

    private fun addGeofence(
        geofencingClient: GeofencingClient,
        geofencingRequest: GeofencingRequest,
        geofencePendingIntent: PendingIntent
    ) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                Constants.REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                Constants.REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.i(TAG, "Successfully added geofence.")
            }
            addOnFailureListener {
                Log.e(TAG, "Failed to add geofence: ${it.message}")
            }
        }
    }

    private fun removeGeofences(geofencingClient: GeofencingClient) {
        geofencingClient.removeGeofences(
            listOf(
                Constants.GEOFENCE_REQUEST_ID_FIRST_POINT,
                Constants.GEOFENCE_REQUEST_ID_LAST_POINT
            )).run {
            addOnFailureListener {
                Log.e(TAG, "Failed to remove geofences: ${it.message}")
            }
        }
    }

    private fun onMyLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        myLocationMarker: Marker
    ) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constants.REQUEST_PERMISSIONS_REQUEST_CODE
            )
            return
        }
        fusedLocationProviderClient.lastLocation.run {
            addOnSuccessListener { location ->
                if (location == null) {
                    Toast.makeText(requireContext(),
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
                Toast.makeText(requireContext(), "Valamilyen hiba történt.", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Failed to get last known location: ${it.message}")
            }
        }
    }

    private fun initMap() {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.hikeMap
        // TODO: center on the given route
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // TODO: handle permission more nicely -> https://developer.android.com/training/permissions/requesting
        MapUtils.onRequestPermissionsResult(
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

    companion object {
        private const val TAG = "HikeFragment"
    }
}