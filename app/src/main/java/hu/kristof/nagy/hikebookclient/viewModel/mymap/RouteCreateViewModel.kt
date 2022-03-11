package hu.kristof.nagy.hikebookclient.viewModel.mymap

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

class RouteCreateViewModel : ViewModel(){
    private val markers = ArrayList<Marker>()
    private val polylines = ArrayList<Polyline>()

    private var _invalidateMap = MutableLiveData(false)
    val invalidateMap: LiveData<Boolean>
        get() = _invalidateMap

    fun onSingleTap(
        newMarker: Marker,
        p: GeoPoint?,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>
    ) {
        // add new marker
        newMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        newMarker.isDraggable = true
        newMarker.position = p
        newMarker.icon = markerIcon
        markers.add(newMarker)
        overlays.add(newMarker)

        if (markers.size > 1) {
            // change previous marker's icon
            val prevMarker = markers[markers.size - 2]
            prevMarker.icon = setMarkerIcon

            // connect the new point with the previous one
            val points = ArrayList<GeoPoint>()
            points.add(prevMarker.position)
            points.add(newMarker.position)
            val polyline = Polyline()
            polyline.setPoints(points)
            polylines.add(polyline)
            overlays.add(polyline)
        }
    }

    fun onMarkerDragEnd(
        marker: Marker
    ) {
        if (markers.size == 1)
            return

        if (markers.first() == marker) {
            refreshNextPolyline(0)
        } else if (markers.last() == marker) {
            refreshPrevPolyline(markers.size - 1)
        } else {
            val idx = markers.indexOf(marker)
            refreshPrevPolyline(idx)
            refreshNextPolyline(idx)
        }
        _invalidateMap.value = !_invalidateMap.value!!
    }

    private fun refreshPrevPolyline(idx: Int) {
        val prevPoints = ArrayList<GeoPoint>()
        prevPoints.add(markers[idx - 1].position)
        prevPoints.add(markers[idx].position)
        polylines[idx - 1].setPoints(prevPoints)
        polylines[idx - 1].isVisible = true
    }

    private fun refreshNextPolyline(idx: Int) {
        val nextPoints = ArrayList<GeoPoint>()
        nextPoints.add(markers[idx].position)
        nextPoints.add(markers[idx + 1].position)
        polylines[idx].setPoints(nextPoints)
        polylines[idx].isVisible = true
    }

    fun onMarkerDragStart(
        marker: Marker
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
        _invalidateMap.value = !_invalidateMap.value!!
    }

    fun onDelete(
        marker: Marker,
        markerIcon: Drawable
    ): Boolean {
        if (markers.last() == marker) {
            markers.removeLast()
            if (markers.isNotEmpty()) {
                markers.last().icon = markerIcon
                polylines.last().isVisible = false
                polylines.removeLast()
            }
            _invalidateMap.value = !_invalidateMap.value!!
            return true
        } else {
            return false
        }
    }
}