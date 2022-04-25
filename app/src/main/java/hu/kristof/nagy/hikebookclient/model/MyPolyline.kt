package hu.kristof.nagy.hikebookclient.model

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

data class MyPolyline(val polyline: Polyline, val points: List<GeoPoint>) {
    companion object {
        fun newInstance(points: List<GeoPoint>): MyPolyline {
            val polyline = Polyline().apply {
                setPoints(points)
            }
            return MyPolyline(polyline, points)
        }

        fun from(polyline: Polyline): MyPolyline {
            return MyPolyline(polyline, polyline.actualPoints)
        }
    }
}
