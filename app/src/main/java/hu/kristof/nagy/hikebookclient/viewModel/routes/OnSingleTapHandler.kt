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
        MyMarker(newMarker, newMarkerType, newMarkerTitle).also { myMarker ->
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
            }.also { polyline ->
                polylines.add(polyline)
                overlays.add(polyline)
            }
        }
    }
}