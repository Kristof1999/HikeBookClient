package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

/**
 * A ViewModel that helps to
 * set, drag, and delete
 * a marker on the map.
 */
abstract class RouteViewModel : ViewModel() {
    protected abstract val markers: MutableList<MyMarker>
    protected abstract val polylines: MutableList<Polyline>

    var markerType = MarkerType.NEW
    var markerTitle = ""

    /**
     * Handles single tap, and resets marker's title to empty.
     */
    fun onSingleTap(
        newMarker: Marker,
        p: GeoPoint?,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>
    ) {
       val handler = when (markerType) {
           MarkerType.TEXT -> OnSingleTapHandlerTextMarkerTypeDecorator(
               OnSingleTapHandler()
           )
           else -> OnSingleTapHandler()
       }

       handler.handle(
           newMarker, markerType, markerTitle,
           p, markerIcon, setMarkerIcon, overlays,
           markers, polylines
       )

        markerTitle = ""
    }

    /**
     * Reconnects the dragged marker with its neighbors.
     * @param marker the dragged marker
     */
    fun onMarkerDragEnd(marker: Marker) {
        if (markers.size == 1)
            return

        if (markers.first().marker == marker) {
            refreshNextPolyline(0, markers, polylines)
        } else if (markers.last().marker == marker) {
            refreshPrevPolyline(markers.size - 1, markers, polylines)
        } else {
            val idx = markers.indexOf(
                markers.filter { it.marker == marker }.get(0)
            )
            refreshPrevPolyline(idx, markers, polylines)
            refreshNextPolyline(idx, markers, polylines)
        }
    }

    private fun refreshPrevPolyline(
        idx: Int,
        markers: MutableList<MyMarker>,
        polylines: MutableList<Polyline>
    ) = with(polylines[idx - 1]) {
        setPoints(listOf(
            markers[idx - 1].marker.position,
            markers[idx].marker.position
        ))
        isVisible = true
    }

    private fun refreshNextPolyline(
        idx: Int,
        markers: MutableList<MyMarker>,
        polylines: MutableList<Polyline>
    ) = with(polylines[idx]) {
        setPoints(listOf(
            markers[idx].marker.position,
            markers[idx + 1].marker.position
        ))
        isVisible = true
    }

    /**
     * Disconnects the to be dragged marker from its neighbors.
     * @param marker marker to be dragged
     */
    fun onMarkerDragStart(marker: Marker) {
        if (markers.size == 1)
            return

        if (markers.first().marker == marker) {
            polylines.first().isVisible = false
        } else if (markers.last().marker == marker) {
            polylines.last().isVisible = false
        } else {
            val idx = markers.indexOf(
                markers.filter { it.marker == marker }.get(0)
            )
            polylines[idx - 1].isVisible = false
            polylines[idx].isVisible = false
        }
    }

    /**
     * Deletes the last marker, updates it's neighbors icon and type if needed,
     * and also removes the polyline connecting the last two markers.
     * @param markerIcon icon to set for the last marker after deletion,
     *                   if the last marker's type was SET
     * @param marker marker to be deleted
     * @return true if marker was the last marker in markers
     */
    fun onDelete(
        markerIcon: Drawable,
        marker: Marker
    ): Boolean {
        if (markers.last().marker == marker) {
            markers.removeLast()
            if (markers.isNotEmpty()) {
                if (markers.last().type == MarkerType.SET) {
                    markers.last().marker.icon = markerIcon
                    markers[markers.size - 1] = MyMarker(
                        markers.last().marker, MarkerType.NEW, markers.last().title
                    )
                }
                polylines.last().isVisible = false
                polylines.removeLast()
            }
            return true
        } else {
            return false
        }
    }
}