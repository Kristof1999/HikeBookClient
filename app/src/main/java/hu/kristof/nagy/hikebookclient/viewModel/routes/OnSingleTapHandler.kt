package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.graphics.drawable.Drawable
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.view.routes.customize
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

class OnSingleTapHandler : IOnSingleTapHandler {
    /**
     * Customizes the newly added marker.
     * Polylines are used to connect the new marker with the previous one.
     * @param newMarker the new marker to customize and add to map
     * @param p the point where the new marker should be added
     * @param markerIcon icon of new markers
     * @param setMarkerIcon icon of set (not new, previously added) markers
     * @param overlays overlays of the map for which to add the new markers and polylines
     */
    override fun handle(
        newMarker: Marker,
        newMarkerType: MarkerType,
        newMarkerTitle: String,
        p: GeoPoint?,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>,
        markers: MutableList<MyMarker>,
        polylines: MutableList<Polyline>
    ) {
        // add new marker
        newMarker.customize(newMarkerTitle, markerIcon, p!!)
        MyMarker(newMarker, newMarkerType, newMarkerTitle).let { myMarker ->
            markers.add(myMarker)
        }
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
            Polyline().apply {
                setPoints(listOf(
                    prevMarker.position, newMarker.position
                ))
            }.let { polyline ->
                polylines.add(polyline)
                overlays.add(polyline)
            }
        }
    }
}