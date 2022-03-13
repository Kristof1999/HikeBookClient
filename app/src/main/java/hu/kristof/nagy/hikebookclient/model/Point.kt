package hu.kristof.nagy.hikebookclient.model

import org.osmdroid.util.GeoPoint

data class Point(val latitude: Double, val longitude: Double) {
    companion object {
        fun from(p: GeoPoint): Point {
            return Point(p.latitude, p.longitude)
        }
    }
}