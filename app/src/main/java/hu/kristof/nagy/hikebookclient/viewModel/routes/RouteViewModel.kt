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
    protected val _markers = mutableListOf<MyMarker>()
    protected val _myPolylines = mutableListOf<MyPolyline>()
    val markers: List<MyMarker>
        get() = _markers
    val myPolylines: List<MyPolyline>
        get() = _myPolylines

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
           _markers, _myPolylines
       )

        markerTitle = ""
    }

    /**
     * Reconnects the dragged marker with its neighbors.
     * @param marker the dragged marker
     */
    fun onMarkerDragEnd(marker: Marker) {
        if (_markers.size == 1)
            return

        if (_markers.first().marker == marker) {
            refreshNextPolyline(0, _markers, _myPolylines)
        } else if (_markers.last().marker == marker) {
            refreshPrevPolyline(_markers.size - 1, _markers, _myPolylines)
        } else {
            val idx = _markers.indexOf(
                _markers.filter { it.marker == marker }[0]
            )
            refreshPrevPolyline(idx, _markers, _myPolylines)
            refreshNextPolyline(idx, _markers, _myPolylines)
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
        if (_markers.size == 1)
            return

        if (_markers.first().marker == marker) {
            _myPolylines.first().polyline.isVisible = false
        } else if (_markers.last().marker == marker) {
            _myPolylines.last().polyline.isVisible = false
        } else {
            val idx = _markers.indexOf(
                _markers.filter { it.marker == marker }[0]
            )
            _myPolylines[idx - 1].polyline.isVisible = false
            _myPolylines[idx].polyline.isVisible = false
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
        if (_markers.last().marker == marker) {
            _markers.removeLast()
            if (_markers.isNotEmpty()) {
                if (_markers.last().type == MarkerType.SET) {
                    _markers.last().marker.icon = markerIcon
                    _markers[_markers.size - 1] = MyMarker(
                        _markers.last().marker, MarkerType.NEW, _markers.last().title
                    )
                }
                _myPolylines.last().polyline.isVisible = false
                _myPolylines.removeLast()
            }
            return true
        } else {
            return false
        }
    }
}