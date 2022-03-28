// based on:
// https://developer.android.com/training/location/retrieve-current

package hu.kristof.nagy.hikebookclient.view.hike

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikeBinding
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class HikeFragment : Fragment() {
    private lateinit var map: MapView
    private lateinit var binding: FragmentHikeBinding
    //private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_hike, container, false
        )
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

        val myLocationMarker = Marker(map)
        myLocationMarker.icon = AppCompatResources.getDrawable(requireContext(),
            org.osmdroid.bonuspack.R.drawable.person
        )
        map.overlays.add(myLocationMarker)

        val fusedLocationProviderClient =  LocationServices
            .getFusedLocationProviderClient(requireContext())
        binding.hikeMyLocationFab.setOnClickListener {
            onMyLocation(fusedLocationProviderClient, myLocationMarker)
        }

        val args: HikeFragmentArgs by navArgs()
        val polyLine = args.userRoute.toPolyline()
        map.overlays.add(polyLine)
        map.invalidate()
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
        // TODO: add fail/deny message -> request handler
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val controller = map.controller
            val p = GeoPoint(location.latitude, location.longitude)
            controller.setCenter(p)
            myLocationMarker.position = p
        }
    }

    private fun initMap() {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.hikeMap
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MapUtils.onRequestPermissionsResult(
            requestCode, permissions, grantResults, requireActivity()
        )
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        //locationOverlay.enableMyLocation()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        //locationOverlay.disableMyLocation()
    }
}