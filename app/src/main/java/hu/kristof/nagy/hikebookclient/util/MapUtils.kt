package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.infowindow.InfoWindow

object MapUtils {
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
        val markerIcon = MarkerUtils.getMarkerIcon(viewModel.markerType, context.resources)
        val setMarkerIcon = MarkerUtils.getMarkerIcon(MarkerType.SET, context.resources)
        viewModel.onSingleTap(newMarker, p, markerIcon, setMarkerIcon, map.overlays)
        MarkerUtils.setMarkerListeners(context, map, deleteSwitch, newMarker, viewModel)
        map.invalidate()
        return true
    }

    /**
     * Adds markers and polylines to the provided lists. The markers will be draggable.
     * Polylines are used to connect the new marker with the previous one.
     * @param newMarker the new marker to customize and add to map
     * @param newMarkerType the type of the new marker
     * @param p the point where the new marker should be added
     * @param markerIcon icon of new markers
     * @param setMarkerIcon icon of set (not new, previously added) markers
     * @param overlays overlays of the map for which to add the new markers and polylines
     * @param markers list where the new markers should be added,
     *        and the markers which have been previously added.
     *        The list should preserve the order in which markers were added.
     * @param polylines list where the new polylines should be added,
     *        and the polylines which have been previously added.
     *        The list should preserve the order in which polylines were added.
     */
    fun onSingleTapLogicHandler(
        newMarker: Marker,
        newMarkerType: MarkerType,
        newMarkerTitle: String,
        p: GeoPoint,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>,
        markers: ArrayList<MyMarker>,
        polylines: ArrayList<Polyline>
    ) {
        // add new marker
        val myMarker = MyMarker(newMarker, newMarkerType, newMarkerTitle)
        MarkerUtils.customizeMarker(myMarker, markerIcon, p)
        markers.add(myMarker)
        overlays.add(newMarker)

        if (markers.size > 1) {
            // change previous marker's icon and type if it was new
            val prevMarker = markers[markers.size - 2].marker
            val prevMarkerType = markers[markers.size - 2].type
            if (prevMarkerType == MarkerType.NEW) {
                prevMarker.icon = setMarkerIcon
                markers[markers.size - 2] = MyMarker(prevMarker, MarkerType.SET, "")
            }

            // connect the new point with the previous one
            val polyline = Polyline()
            polyline.setPoints(listOf(
                prevMarker.position, newMarker.position
            ))
            polylines.add(polyline)
            overlays.add(polyline)
        }
    }

    fun onRoutesLoad(
        res: Result<List<Route>>,
        context: Context?,
        map: MapView
    ) {
        handleResult(context, res) { routes ->
            val folderOverlay = FolderOverlay()
            routes.forEach { route ->
                val polyline = route.toPolyline()
                folderOverlay.add(polyline)
                polyline.setOnClickListener { _, _, _ ->
                    Toast.makeText(context, route.routeName, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener true
                }
            }
            map.overlays.add(folderOverlay)
            map.invalidate()
        }
    }
}

fun MapView.setMapCenterOnPolylineStart(polyline: Polyline) {
    val start = polyline.actualPoints.get(0)
    val mapController = controller
    mapController.setCenter(start)
}

fun MapView.setMapCenterOnPolylineCenter(polyline: Polyline) {
    val center = polyline.bounds.centerWithDateLine
    val mapController = controller
    mapController.setCenter(center)
}

fun MapView.setZoomForPolyline(polyline: Polyline) {
    val distance = polyline.distance
    val zoomLevel: Double = when {
        distance < 500 -> 18.0
        distance < 1000 -> 16.5
        distance < 5000 -> 15.0
        distance < 10000 -> 13.0
        distance < 20000 -> 10.0
        else -> 9.0
    }
    val mapController = controller
    mapController.setZoom(zoomLevel)
}

fun MapView.setStartZoomAndCenter() = controller.apply {
    setZoom(Constants.START_ZOOM)
    setCenter(Constants.START_POINT)
}

fun MapView.addCopyRightOverlay() {
    val copyrightOverlay = CopyrightOverlay(context)
    overlays.add(copyrightOverlay)
    invalidate()
}