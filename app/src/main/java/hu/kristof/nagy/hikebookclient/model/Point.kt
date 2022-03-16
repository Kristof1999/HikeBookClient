package hu.kristof.nagy.hikebookclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

@Parcelize
data class Point(val latitude: Double, val longitude: Double) : Parcelable {

    fun toGeoPoint(): GeoPoint {
        return GeoPoint(latitude, longitude)
    }

    companion object {
        fun from(marker: Marker): Point {
            val p = marker.position
            return Point(p.latitude, p.longitude)
        }
    }
}