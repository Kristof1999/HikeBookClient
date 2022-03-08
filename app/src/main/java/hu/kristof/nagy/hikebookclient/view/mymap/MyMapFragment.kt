// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki

package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.hikebookclient.R
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MyMapFragment : Fragment() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = requireActivity().findViewById(R.id.myMap)

        val mapController = map.controller
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
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