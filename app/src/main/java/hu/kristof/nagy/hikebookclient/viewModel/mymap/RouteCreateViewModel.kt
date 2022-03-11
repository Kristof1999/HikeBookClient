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
    var isDeleteOn = false // TODO: handle interruptions: device rotation, ...

    private var _invalidateMap = MutableLiveData<Boolean>(false)
    val invalidateMap: LiveData<Boolean>
        get() = _invalidateMap

    fun onSingleTap(
        newMarker: Marker,
        p: GeoPoint?,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>
    ) {
        if (isDeleteOn)
            return

        // add new marker
        handleNewMarker(newMarker, p, markerIcon)
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

    fun handleNewMarker(
        newMarker: Marker,
        p: GeoPoint?,
        icon: Drawable
    ) {
        newMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        newMarker.isDraggable = true
        newMarker.position = p
        newMarker.icon = icon
        markers.add(newMarker)

    }

    fun onMarkerDragEnd(
        marker: Marker
    ) {
        if (markers.size == 1)
            return

        val idx = markers.indexOf(marker)
        if (idx == 0) {
            val points = ArrayList<GeoPoint>()
            points.add(marker.position)
            points.add(markers[idx + 1].position)
            polylines[idx].setPoints(points)
            polylines[idx].isVisible = true
        } else if (idx == markers.size - 1) {
            val points = ArrayList<GeoPoint>()
            points.add(markers[idx - 1].position)
            points.add(marker.position)
            polylines[idx - 1].setPoints(points)
            polylines[idx - 1].isVisible = true
        } else {
            val prevPoints = ArrayList<GeoPoint>()
            prevPoints.add(markers[idx - 1].position)
            prevPoints.add(marker.position)
            polylines[idx - 1].setPoints(prevPoints)
            polylines[idx - 1].isVisible = true

            val nextPoints = ArrayList<GeoPoint>()
            nextPoints.add(marker.position)
            nextPoints.add(markers[idx + 1].position)
            polylines[idx].setPoints(nextPoints)
            polylines[idx].isVisible = true
        }
        _invalidateMap.value = !_invalidateMap.value!!
    }

    fun onMarkerDragStart(
        marker: Marker
    ) {
        if (markers.size == 1)
            return

        val idx = markers.indexOf(marker)
        if (idx == 0) {
            polylines[idx].isVisible = false
        } else if (idx == markers.size - 1) {
            polylines[idx - 1].isVisible = false
        } else {
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