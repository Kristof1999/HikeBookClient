package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.graphics.drawable.Drawable
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

abstract class OnSingleTapHandlerDecorator(
    private val onSingleTapHandler: IOnSingleTapHandler
) : IOnSingleTapHandler {
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
        onSingleTapHandler.handle(
            newMarker, newMarkerType, newMarkerTitle,
            p, markerIcon, setMarkerIcon,
            overlays, markers, polylines
        )
    }
}