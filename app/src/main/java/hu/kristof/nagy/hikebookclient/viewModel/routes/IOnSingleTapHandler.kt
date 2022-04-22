package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.graphics.drawable.Drawable
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

interface IOnSingleTapHandler {
    fun handle(
        newMarker: Marker,
        newMarkerType: MarkerType,
        newMarkerTitle: String,
        p: GeoPoint?,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>,
        markers: MutableList<MyMarker>,
        polylines: MutableList<Polyline>
    )
}