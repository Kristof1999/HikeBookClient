package hu.kristof.nagy.hikebookclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.osmdroid.util.GeoPoint

@Parcelize
data class Point(val latitude: Double, val longitude: Double, val type: MarkerType) : Parcelable {

    fun toGeoPoint(): GeoPoint {
        return GeoPoint(latitude, longitude)
    }

    companion object {
        fun from(marker: MyMarker): Point {
            val p = marker.marker.position
            return Point(p.latitude, p.longitude, marker.type)
        }
    }
}