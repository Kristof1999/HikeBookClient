package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.MyPolyline
import hu.kristof.nagy.hikebookclient.view.routes.MarkerType
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

class OnSingleTapHandlerTextMarkerTypeDecorator(
    onSingleTapHandler: IOnSingleTapHandler
) : OnSingleTapHandlerDecorator(onSingleTapHandler) {
    override fun handle(
        newMarker: Marker,
        newMarkerType: MarkerType,
        newMarkerTitle: String,
        p: GeoPoint?,
        resources: Resources,
        overlays: MutableList<Overlay>,
        markers: MutableList<MyMarker>,
        myPolylines: MutableList<MyPolyline>
    ) {
        var markerType = newMarkerType

        if (newMarkerTitle.isEmpty()) {
            markerType = MarkerType.NEW
        }

        super.handle(
            newMarker, markerType, newMarkerTitle,
            p, resources,
            overlays, markers, myPolylines
        )

        _setSpinnerToDefault.value = !_setSpinnerToDefault.value!!
    }

    companion object {
        private val _setSpinnerToDefault = MutableLiveData(true)
        val setSpinnerToDefault: LiveData<Boolean>
            get() = _setSpinnerToDefault
    }
}