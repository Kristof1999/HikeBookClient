package hu.kristof.nagy.hikebookclient.util

import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.core.app.ActivityCompat
import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.model.MyMarker
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

object MapUtils {
    private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        activity: Activity
    ) {
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            );
        }
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
    fun onSingleTap(
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
            val points = ArrayList<GeoPoint>()
            points.add(prevMarker.position)
            points.add(newMarker.position)
            val polyline = Polyline()
            polyline.setPoints(points)
            polylines.add(polyline)
            overlays.add(polyline)
        }
    }
}

fun MapView.setStartZoomAndCenter() {
    val mapController = controller
    mapController.setZoom(Constants.START_ZOOM)
    mapController.setCenter(Constants.START_POINT)
}

fun MapView.addCopyRightOverlay() {
    val copyrightOverlay = CopyrightOverlay(context)
    overlays.add(copyrightOverlay)
    invalidate()
}