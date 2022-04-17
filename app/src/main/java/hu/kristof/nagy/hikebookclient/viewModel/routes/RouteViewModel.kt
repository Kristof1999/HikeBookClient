package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.MarkerUtils
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

/**
 * A ViewModel that helps to
 * set, drag, and delete
 * a marker on the map.
 */
abstract class RouteViewModel : ViewModel() {
    protected abstract val markers: ArrayList<MyMarker>
    protected abstract val polylines: ArrayList<Polyline>

    var markerType: MarkerType = MarkerType.NEW
    /**
     * Single use title. After usage for one marker, it will be reset to empty string.
     */
    var markerTitle: String = ""

    fun onSingleTap(
        newMarker: Marker,
        p: GeoPoint?,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>
    ) {
        MapUtils.onSingleTapLogicHandler(
            newMarker, markerType, markerTitle, p!!, markerIcon, setMarkerIcon, overlays, markers, polylines
        )
        markerTitle = ""
    }

    fun onMarkerDragEnd(marker: Marker) =
        MarkerUtils.onMarkerDragEnd(marker, markers.map {it.marker} as ArrayList<Marker>, polylines)

    fun onMarkerDragStart(marker: Marker) =
        MarkerUtils.onMarkerDragStart(marker, markers.map {it.marker} as ArrayList<Marker>, polylines)

    fun onDelete(
        resources: Resources,
        marker: Marker
    ): Boolean = MarkerUtils.onDeleteLogicHandler(resources, marker, markers, polylines)
}