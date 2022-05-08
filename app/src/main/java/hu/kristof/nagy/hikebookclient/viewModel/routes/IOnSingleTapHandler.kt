package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.content.res.Resources
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.MyPolyline
import hu.kristof.nagy.hikebookclient.view.routes.MarkerType
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

interface IOnSingleTapHandler {
    fun handle(
        newMarker: Marker,
        newMarkerType: MarkerType,
        newMarkerTitle: String,
        p: GeoPoint?,
        resources: Resources,
        overlays: MutableList<Overlay>,
        markers: MutableList<MyMarker>,
        myPolylines: MutableList<MyPolyline>
    )
}