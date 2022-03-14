package hu.kristof.nagy.hikebookclient.viewModel.mymap

import android.graphics.drawable.Drawable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.network.Service
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MapHelper
import hu.kristof.nagy.hikebookclient.util.MarkerUtils
import hu.kristof.nagy.hikebookclient.util.RouteUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@HiltViewModel
class RouteEditViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val service: Service
    ) : ViewModel() {
    private lateinit var markers: ArrayList<Marker>
    private lateinit var polylines: ArrayList<Polyline>

    private var _invalidateMap = MutableLiveData(false)
    val invalidateMap: LiveData<Boolean>
        get() = _invalidateMap

    private var _routeEditRes = MutableLiveData<Boolean>()
    val routeEditRes: LiveData<Boolean>
        get() = _routeEditRes

    fun setup(markers: ArrayList<Marker>, polylines: ArrayList<Polyline>) {
        this.markers = markers
        this.polylines = polylines
    }

    fun onRouteEdit(route: Route) {
        RouteUtils.checkRoute(route)
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                preferences[Constants.DATA_STORE_USER_NAME]
            }.collect { userName ->
                _routeEditRes.value = service.editRoute(userName!!, route.routeName, route)
            }
        }
    }

    fun onSingleTap(
        newMarker: Marker,
        p: GeoPoint?,
        markerIcon: Drawable,
        setMarkerIcon: Drawable,
        overlays: MutableList<Overlay>
    ) {
       MapHelper.onSingleTap(
           newMarker, p, markerIcon, setMarkerIcon, overlays, markers, polylines
       )
    }

    fun onMarkerDragEnd(
        marker: Marker
    ) {
        MarkerUtils.onMarkerDragEnd(marker, markers, polylines)
        _invalidateMap.value = !_invalidateMap.value!!
    }

    fun onMarkerDragStart(
        marker: Marker
    ) {
        MarkerUtils.onMarkerDragStart(marker, markers, polylines)
        _invalidateMap.value = !_invalidateMap.value!!
    }

    fun onDelete(
        marker: Marker,
        markerIcon: Drawable
    ): Boolean {
        if (MarkerUtils.onDelete(marker, markerIcon, markers, polylines)) {
            _invalidateMap.value = !_invalidateMap.value!!
            return true
        } else {
            return false
        }
    }
}