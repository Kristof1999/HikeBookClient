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
    protected abstract val _markers: MutableList<MyMarker>
    protected abstract val _polylines: MutableList<Polyline>
    val markers: List<MyMarker>
        get() = _markers
    val polylines: List<Polyline>
        get() = _polylines

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
           _markers, _polylines
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
            refreshNextPolyline(0, _markers, _polylines)
        } else if (_markers.last().marker == marker) {
            refreshPrevPolyline(_markers.size - 1, _markers, _polylines)
        } else {
            val idx = _markers.indexOf(
                _markers.filter { it.marker == marker }[0]
            )
            refreshPrevPolyline(idx, _markers, _polylines)
            refreshNextPolyline(idx, _markers, _polylines)
        }
    }

    private fun refreshPrevPolyline(
        idx: Int,
        markers: List<MyMarker>,
        polylines: List<Polyline>
    ) = with(polylines[idx - 1]) {
        setPoints(listOf(
            markers[idx - 1].marker.position,
            markers[idx].marker.position
        ))
        isVisible = true
    }

    private fun refreshNextPolyline(
        idx: Int,
        markers: List<MyMarker>,
        polylines: List<Polyline>
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
        if (_markers.size == 1)
            return

        if (_markers.first().marker == marker) {
            _polylines.first().isVisible = false
        } else if (_markers.last().marker == marker) {
            _polylines.last().isVisible = false
        } else {
            val idx = _markers.indexOf(
                _markers.filter { it.marker == marker }[0]
            )
            _polylines[idx - 1].isVisible = false
            _polylines[idx].isVisible = false
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
                _polylines.last().isVisible = false
                _polylines.removeLast()
            }
            return true
        } else {
            return false
        }
    }
}