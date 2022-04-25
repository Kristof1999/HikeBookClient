package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.MyPolyline
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

/**
 * A ViewModel that helps to
 * set, drag, and delete
 * a marker on the map.
 */
open class RouteViewModel : ViewModel() {
    var myMarkers = mutableListOf<MyMarker>()
    var myPolylines = mutableListOf<MyPolyline>()

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
           myMarkers, myPolylines
       )

        markerTitle = ""
    }

    /**
     * Reconnects the dragged marker with its neighbors.
     * @param marker the dragged marker
     */
    fun onMarkerDragEnd(marker: Marker) {
        if (myMarkers.size == 1)
            return

        if (myMarkers.first().marker == marker) {
            refreshNextPolyline(0, myMarkers, myPolylines)
        } else if (myMarkers.last().marker == marker) {
            refreshPrevPolyline(
                myMarkers.size - 1,
                myMarkers, myPolylines)
        } else {
            val idx = myMarkers.indexOf(
                myMarkers.filter { it.marker == marker }[0]
            )
            refreshPrevPolyline(idx, myMarkers, myPolylines)
            refreshNextPolyline(idx, myMarkers, myPolylines)
        }
    }

    private fun refreshPrevPolyline(
        idx: Int,
        markers: List<MyMarker>,
        myPolylines: List<MyPolyline>
    ) = with(myPolylines[idx - 1].polyline) {
        setPoints(listOf(
            markers[idx - 1].marker.position,
            markers[idx].marker.position
        ))
        isVisible = true
    }

    private fun refreshNextPolyline(
        idx: Int,
        markers: List<MyMarker>,
        myPolylines: List<MyPolyline>
    ) = with(myPolylines[idx].polyline) {
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
        if (myMarkers.size == 1)
            return

        if (myMarkers.first().marker == marker) {
            myPolylines.first().polyline.isVisible = false
        } else if (myMarkers.last().marker == marker) {
            myPolylines.last().polyline.isVisible = false
        } else {
            val idx = myMarkers.indexOf(
                myMarkers.filter { it.marker == marker }[0]
            )
            myPolylines[idx - 1].polyline.isVisible = false
            myPolylines[idx].polyline.isVisible = false
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
        if (myMarkers.last().marker == marker) {
            myMarkers.removeLast()
            if (myMarkers.isNotEmpty()) {
                if (myMarkers.last().type == MarkerType.SET) {
                    myMarkers.last().marker.icon = markerIcon
                    myMarkers[myMarkers.size - 1] = MyMarker(
                        myMarkers.last().marker, MarkerType.NEW, myMarkers.last().title
                    )
                }
                myPolylines.last().polyline.isVisible = false
                myPolylines.removeLast()
            }
            return true
        } else {
            return false
        }
    }
}