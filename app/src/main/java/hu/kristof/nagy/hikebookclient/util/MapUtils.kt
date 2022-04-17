package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.Toast
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Polyline

fun onRoutesLoad(
    res: Result<List<Route>>,
    context: Context?,
    map: MapView
) {
    handleResult(context, res) { routes ->
        map.overlays.clear()
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