package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
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
        markers: List<Marker>,
        polylines: List<Polyline>
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
        markers: List<Marker>,
        polylines: List<Polyline>
    ) = with(polylines[idx - 1]) {
        setPoints(listOf(
            markers[idx - 1].position,
            markers[idx].position
        ))
        isVisible = true
    }

    private fun refreshNextPolyline(
        idx: Int,
        markers: List<Marker>,
        polylines: List<Polyline>
    ) = with(polylines[idx]) {
        setPoints(listOf(
            markers[idx].position,
            markers[idx + 1].position
        ))
        isVisible = true
    }

    /**
     * Disconnects the to be dragged marker from its neighbors.
     * @param marker marker to be dragged
     * @param markers list of markers in order they were added to the map
     * @param polylines list of polylines in order they were added to the map
     */
    fun onMarkerDragStart(
        marker: Marker,
        markers: List<Marker>,
        polylines: List<Polyline>
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

    fun onDeleteViewHandler(
        context: Context,
        marker: Marker,
        mapView: MapView,
        viewModel: RouteViewModel
    ) {
        if (viewModel.onDelete(context.resources, marker)) {
            marker.remove(mapView)
            mapView.invalidate()
        } else {
            Toast.makeText(
                context, context.getString(R.string.not_last_point_delete_error_msg), Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Deletes the last marker, updates it's neighbors icon and type if needed,
     * and also removes the polyline connecting the last two markers.
     * @param marker marker to be deleted
     * @param markerIcon icon of marker to set for the deleted marker's neighbour icon,
     *                   if the neighbour's type was set
     * @param markers list of markers in order they were added to the map
     * @param polylines list of polylines in order they were added to the map
     * @return true if marker was the last marker in markers
     */
    fun onDeleteLogicHandler(
        resources: Resources,
        marker: Marker,
        markers: ArrayList<MyMarker>,
        polylines: ArrayList<Polyline>
    ): Boolean {
        if (markers.last().marker == marker) {
            markers.removeLast()
            if (markers.isNotEmpty()) {
                if (markers.last().type == MarkerType.SET) {
                    markers.last().marker.icon = getMarkerIcon(MarkerType.NEW, resources)
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

    fun getMarkerIcon(type: MarkerType, resources: Resources): Drawable = when(type) {
        MarkerType.NEW -> ResourcesCompat.getDrawable(resources, R.drawable.marker_image, null)!!
        MarkerType.CASTLE -> ResourcesCompat.getDrawable(resources, R.drawable.castle_image, null)!!
        MarkerType.LOOKOUT -> ResourcesCompat.getDrawable(resources, R.drawable.landscape_image, null)!!
        MarkerType.TEXT -> ResourcesCompat.getDrawable(resources, R.drawable.text_marker, null)!!
        MarkerType.SET -> ResourcesCompat.getDrawable(resources, R.drawable.set_marker_image, null)!!
    }

    fun makePolylineFromLastTwo(markers: List<MyMarker>): Polyline =
        Polyline().apply {
            setPoints(listOf(
                markers[markers.size - 2].marker.position,
                markers[markers.size - 1].marker.position
            ))
        }
}

fun Marker.customize(title: String, icon: Drawable, p: GeoPoint) {
    setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
    isDraggable = true
    position = p
    this.title = title
    this.icon = icon
}

fun Marker.setListeners(
    context: Context,
    map: MapView,
    deleteSwitch: SwitchCompat,
    viewModel: RouteViewModel
) {
    setOnMarkerClickListener(Marker.OnMarkerClickListener { marker, mapView ->
        if (deleteSwitch.isChecked) {
            MarkerUtils.onDeleteViewHandler(context, marker, mapView, viewModel)
        } else {
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
            } else {
                marker.showInfoWindow()
            }
        }
        return@OnMarkerClickListener true
    })

    setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
        override fun onMarkerDrag(marker: Marker?) {

        }

        override fun onMarkerDragEnd(marker: Marker?) {
            if (marker == null)
                return

            viewModel.onMarkerDragEnd(marker)
            map.invalidate()
        }

        override fun onMarkerDragStart(marker: Marker?) {
            if (marker == null)
                return

            viewModel.onMarkerDragStart(marker)
            map.invalidate()
        }
    })
}