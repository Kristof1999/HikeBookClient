package hu.kristof.nagy.hikebookclient.model

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

data class Point(val latitude: Double, val longitude: Double) {

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