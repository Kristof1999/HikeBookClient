// based on:
// https://developer.android.com/training/location/geofencing

package hu.kristof.nagy.hikebookclient.view.hike

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }
        Log.i(TAG, "BroadcastReceiver received intent.")

        val geofenceTransition = geofencingEvent.geofenceTransition

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> inRadius = true
            Geofence.GEOFENCE_TRANSITION_EXIT -> inRadius = false
            else -> Log.e(TAG, "Invalid geofence transition type.")
        }
    }

    companion object {
        private const val TAG = "GeofenceBroadcastRcvr"
        var inRadius = false
    }
}