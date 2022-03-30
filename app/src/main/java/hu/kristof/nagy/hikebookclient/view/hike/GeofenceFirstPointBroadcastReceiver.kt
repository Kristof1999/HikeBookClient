package hu.kristof.nagy.hikebookclient.view.hike

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceFirstPointBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }
        Log.i(TAG, "Received intent.")

        val geofenceTransition = geofencingEvent.geofenceTransition

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_EXIT -> exited = true
            else -> Log.e(TAG, "Invalid geofence transition type: $geofenceTransition")
        }
    }

    companion object {
        private const val TAG = "GeofenceFirstBroadcast"
        var exited = false
    }
}