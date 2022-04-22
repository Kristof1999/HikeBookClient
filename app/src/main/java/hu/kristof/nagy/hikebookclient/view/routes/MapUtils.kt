package hu.kristof.nagy.hikebookclient.view.routes

import android.content.Context
import androidx.appcompat.widget.SwitchCompat
import hu.kristof.nagy.hikebookclient.util.getMarkerIcon
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

fun setMapClickListeners(
    context: Context,
    map: MapView,
    deleteSwitch: SwitchCompat,
    viewModel: RouteViewModel
) {
    val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
            return onSingleTapViewHandler(context, map, deleteSwitch, p, viewModel)
        }

        override fun longPressHelper(p: GeoPoint?): Boolean {
            return true
        }
    })
    map.overlays.add(0, mapEventsOverlay)
    map.invalidate()
}

private fun onSingleTapViewHandler(
    context: Context,
    map: MapView,
    deleteSwitch: SwitchCompat,
    p: GeoPoint?,
    viewModel: RouteViewModel
): Boolean {
    InfoWindow.closeAllInfoWindowsOn(map)

    if (deleteSwitch.isChecked)
        return true

    val newMarker = Marker(map)
    val markerIcon =
        if (viewModel.markerType == MarkerType.TEXT &&
            viewModel.markerTitle.isEmpty()) {
            getMarkerIcon(MarkerType.NEW, context.resources)
        } else {
            getMarkerIcon(viewModel.markerType, context.resources)
        }
    val setMarkerIcon = getMarkerIcon(MarkerType.SET, context.resources)
    viewModel.onSingleTap(newMarker, p, markerIcon, setMarkerIcon, map.overlays)
    newMarker.setListeners(context, map, deleteSwitch, viewModel)
    map.invalidate()
    return true
}