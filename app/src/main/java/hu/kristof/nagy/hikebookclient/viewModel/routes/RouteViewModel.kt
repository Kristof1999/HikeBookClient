package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    protected abstract val _markers: MutableLiveData<MutableList<MyMarker>>
    protected abstract val _polylines: MutableLiveData<MutableList<Polyline>>
    val markers: LiveData<MutableList<MyMarker>>
        get() = _markers
    val polylines: LiveData<MutableList<Polyline>>
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
           _markers.value!!, _polylines.value!!
       )

        markerTitle = ""
    }

    /**
     * Reconnects the dragged marker with its neighbors.
     * @param marker the dragged marker
     */
    fun onMarkerDragEnd(marker: Marker) {
        if (_markers.value!!.size == 1)
            return

        if (_markers.value!!.first().marker == marker) {
            refreshNextPolyline(0, _markers.value!!, _polylines.value!!)
        } else if (_markers.value!!.last().marker == marker) {
            refreshPrevPolyline(_markers.value!!.size - 1, _markers.value!!, _polylines.value!!)
        } else {
            val idx = _markers.value!!.indexOf(
                _markers.value!!.filter { it.marker == marker }[0]
            )
            refreshPrevPolyline(idx, _markers.value!!, _polylines.value!!)
            refreshNextPolyline(idx, _markers.value!!, _polylines.value!!)
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
        if (_markers.value!!.size == 1)
            return

        if (_markers.value!!.first().marker == marker) {
            _polylines.value!!.first().isVisible = false
        } else if (_markers.value!!.last().marker == marker) {
            _polylines.value!!.last().isVisible = false
        } else {
            val idx = _markers.value!!.indexOf(
                _markers.value!!.filter { it.marker == marker }[0]
            )
            _polylines.value!![idx - 1].isVisible = false
            _polylines.value!![idx].isVisible = false
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
        if (_markers.value!!.last().marker == marker) {
            _markers.value!!.removeLast()
            if (_markers.value!!.isNotEmpty()) {
                if (_markers.value!!.last().type == MarkerType.SET) {
                    _markers.value!!.last().marker.icon = markerIcon
                    _markers.value!![_markers.value!!.size - 1] = MyMarker(
                        _markers.value!!.last().marker, MarkerType.NEW, _markers.value!!.last().title
                    )
                }
                _polylines.value!!.last().isVisible = false
                _polylines.value!!.removeLast()
            }
            return true
        } else {
            return false
        }
    }
}