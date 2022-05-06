package hu.kristof.nagy.hikebookclient.view.routes

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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
        // TODO: manually test info windows
        marker.closeInfoWindow()
        if (deleteSwitch.isChecked) {
            onDeleteViewHandler(context, marker, mapView, viewModel)
        } else {
            marker.showInfoWindow()
        }
        return@OnMarkerClickListener true
    })

    setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
        override fun onMarkerDrag(marker: Marker?) {}

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

/**
 * Removes the marker if deletion was successful,
 * otherwise shows an error message.
 */
private fun onDeleteViewHandler(
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