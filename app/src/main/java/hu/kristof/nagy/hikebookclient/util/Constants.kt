package hu.kristof.nagy.hikebookclient.util

import androidx.datastore.preferences.core.stringPreferencesKey
import org.osmdroid.util.GeoPoint

/**
 * Contains constants for maps and datastores.
 */
object Constants {
    const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    /**
     * Budapest
     */
    val START_POINT = GeoPoint(47.2954, 19.227)

    /**
     * Around city level zoom
     */
    const val START_ZOOM = 9.5

    /**
     * Key for user name in preferences datastore
     */
    val DATA_STORE_USER_NAME = stringPreferencesKey("userName")

    const val METRIC_UNIT = "metric"
    const val LANGUAGE = "hu"

    const val GEOFENCE_REQUEST_ID_LAST_POINT = "geofence_last"
    const val GEOFENCE_REQUEST_ID_FIRST_POINT = "geofence_first"
    const val GEOFENCE_RADIUS_IN_METERS = 100f
    const val GEOFENCE_REQUEST_CODE = 0
}