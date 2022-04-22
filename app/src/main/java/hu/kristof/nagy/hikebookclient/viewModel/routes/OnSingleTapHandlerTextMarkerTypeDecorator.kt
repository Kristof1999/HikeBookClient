package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

class OnSingleTapHandlerTextMarkerTypeDecorator(
    private val onSingleTapHandler: IOnSingleTapHandler
) : OnSingleTapHandlerDecorator(onSingleTapHandler) {
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
        var markerType = newMarkerType

        if (newMarkerTitle.isEmpty()) {
            markerType = MarkerType.NEW
        }

        super.handle(
            newMarker, markerType, newMarkerTitle,
            p, markerIcon, setMarkerIcon,
            overlays, markers, polylines
        )

        _setSpinnerToDefault.value = !_setSpinnerToDefault.value!!
    }

    companion object {
        private var _setSpinnerToDefault = MutableLiveData(true)
        val setSpinnerToDefault: LiveData<Boolean>
            get() = _setSpinnerToDefault
    }
}