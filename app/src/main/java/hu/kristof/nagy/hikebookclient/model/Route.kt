package hu.kristof.nagy.hikebookclient.model

import org.osmdroid.views.overlay.Polyline

data class Route(val routeName: String, val points: List<Point>) {
    fun toPolyline(): Polyline {
        val polyline = Polyline()
        polyline.setPoints(points.map {
            it.toGeoPoint()
        })
        return polyline
    }
}