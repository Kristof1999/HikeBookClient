package hu.kristof.nagy.hikebookclient.util

import android.graphics.drawable.Drawable
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

object MarkerUtils {
    /**
     * Reconnects the dragged marker with its neighbors.
     * @param marker the dragged marker
     * @param markers list of markers in order they were added to the map
     * @param polylines list of polylines in order they were added to the map
     */
    fun onMarkerDragEnd(
        marker: Marker,
        markers: ArrayList<Marker>,
        polylines: ArrayList<Polyline>
    ) {
        if (markers.size == 1)
            return

        if (markers.first() == marker) {
            refreshNextPolyline(0, markers, polylines)
        } else if (markers.last() == marker) {
            refreshPrevPolyline(markers.size - 1, markers, polylines)
        } else {
            val idx = markers.indexOf(marker)
            refreshPrevPolyline(idx, markers, polylines)
            refreshNextPolyline(idx, markers, polylines)
        }
    }

    private fun refreshPrevPolyline(
        idx: Int,
        markers: ArrayList<Marker>,
        polylines: ArrayList<Polyline>
    ) {
        val prevPoints = ArrayList<GeoPoint>()
        prevPoints.add(markers[idx - 1].position)
        prevPoints.add(markers[idx].position)
        polylines[idx - 1].setPoints(prevPoints)
        polylines[idx - 1].isVisible = true
    }

    private fun refreshNextPolyline(
        idx: Int,
        markers: ArrayList<Marker>,
        polylines: ArrayList<Polyline>
    ) {
        val nextPoints = ArrayList<GeoPoint>()
        nextPoints.add(markers[idx].position)
        nextPoints.add(markers[idx + 1].position)
        polylines[idx].setPoints(nextPoints)
        polylines[idx].isVisible = true
    }

    /**
     * Disconnects the to be dragged marker from its neighbors.
     * @param marker marker to be dragged
     * @param markers list of markers in order they were added to the map
     * @param polylines list of polylines in order they were added to the map
     */
    fun onMarkerDragStart(
        marker: Marker,
        markers: ArrayList<Marker>,
        polylines: ArrayList<Polyline>
    ) {
        if (markers.size == 1)
            return

        if (markers.first() == marker) {
            polylines.first().isVisible = false
        } else if (markers.last() == marker) {
            polylines.last().isVisible = false
        } else {
            val idx = markers.indexOf(marker)
            polylines[idx - 1].isVisible = false
            polylines[idx].isVisible = false
        }
    }

    /**
     * Deletes the last marker, updates it's neighbors icon, and also removes
     * the polyline connecting the last two markers.
     * @param marker marker to be deleted
     * @param markerIcon icon of marker to set for the deleted marker's icon
     * @param markers list of markers in order they were added to the map
     * @param polylines list of polylines in order they were added to the map
     * @return true if marker was the last marker in markers
     */
    fun onDelete(
        marker: Marker,
        markerIcon: Drawable,
        markers: ArrayList<Marker>,
        polylines: ArrayList<Polyline>
    ): Boolean {
        if (markers.last() == marker) {
            markers.removeLast()
            if (markers.isNotEmpty()) {
                markers.last().icon = markerIcon
                polylines.last().isVisible = false
                polylines.removeLast()
            }
            return true
        } else {
            return false
        }
    }
}