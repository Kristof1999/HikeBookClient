package hu.kristof.nagy.hikebookclient.util

import androidx.datastore.preferences.core.stringPreferencesKey
import org.osmdroid.util.GeoPoint

/**
 * Contains constants for maps and datastores.
 */
object Constants {
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
}