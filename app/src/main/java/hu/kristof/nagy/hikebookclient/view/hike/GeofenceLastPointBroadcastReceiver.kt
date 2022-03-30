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
import java.util.*

class GeofenceLastPointBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
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
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                entered = true
                // TODO: handle edge case -> route is circle, ...
                enterTime = Calendar.getInstance().timeInMillis
            }
            else -> Log.e(TAG, "Invalid geofence transition type: $geofenceTransition")
        }
    }

    companion object {
        private const val TAG = "GeofenceLastBroadcast"
        var entered = false
        var enterTime: Long = -1
    }
}