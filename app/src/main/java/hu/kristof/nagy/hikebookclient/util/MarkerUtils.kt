package hu.kristof.nagy.hikebookclient.util

import android.app.Activity
import android.graphics.drawable.Drawable
import com.example.hikebookclient.R
import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.model.MyMarker
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
     * Deletes the last marker, updates it's neighbors icon and type if needed,
     * and also removes the polyline connecting the last two markers.
     * @param marker marker to be deleted
     * @param markerIcon icon of marker to set for the deleted marker's icon
     * @param markers list of markers in order they were added to the map
     * @param polylines list of polylines in order they were added to the map
     * @return true if marker was the last marker in markers
     */
    fun onDelete(
        marker: Marker,
        markerIcon: Drawable,
        markers: ArrayList<MyMarker>,
        polylines: ArrayList<Polyline>
    ): Boolean {
        if (markers.last().marker == marker) {
            markers.removeLast()
            if (markers.isNotEmpty()) {
                if (markers.last().type == MarkerType.SET) {
                    markers.last().marker.icon = markerIcon
                    markers[markers.size - 1] = MyMarker(markers.last().marker, MarkerType.NEW)
                }
                polylines.last().isVisible = false
                polylines.removeLast()
            }
            return true
        } else {
            return false
        }
    }

    // TODO: use ResourceCompat or sg. else that would allow
    // this method to be called in a viewModel instead of a Fragment
    fun getMarkerIcon(type: MarkerType, activity: Activity): Drawable = when(type) {
        MarkerType.NEW -> activity.getDrawable(R.drawable.marker_image)!!
        MarkerType.CASTLE -> activity.getDrawable(R.drawable.castle_image)!!
        MarkerType.LOOKOUT -> activity.getDrawable(R.drawable.landscape_image)!!
        MarkerType.TEXT -> activity.getDrawable(R.drawable.text_marker)!!
        MarkerType.SET -> activity.getDrawable(R.drawable.set_marker_image)!!
    }
}