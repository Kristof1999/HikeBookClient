package hu.kristof.nagy.hikebookclient.util

import androidx.datastore.preferences.core.stringPreferencesKey
import org.osmdroid.util.GeoPoint

object Constants {
    val START_POINT = GeoPoint(47.2954, 19.227) // Budapest
    const val START_ZOOM = 9.5

    val DATA_STORE_USER_NAME = stringPreferencesKey("userName")
}