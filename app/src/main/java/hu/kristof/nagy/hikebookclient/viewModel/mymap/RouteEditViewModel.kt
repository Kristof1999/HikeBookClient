package hu.kristof.nagy.hikebookclient.viewModel.mymap

import android.graphics.drawable.Drawable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MapUtils
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
    private lateinit var markers: ArrayList<MyMarker>
    private lateinit var polylines: ArrayList<Polyline>

    private var _routeEditRes = MutableLiveData<Boolean>()
    /**
     * Result of route edit attempt.
     */
    val routeEditRes: LiveData<Boolean>
        get() = _routeEditRes

    var markerType: MarkerType = MarkerType.NEW
    /**
     * Single use title. After usage for one marker, it will be reset to empty string.
     */
    var markerTitle: String = ""

    fun setup(markers: ArrayList<MyMarker>, polylines: ArrayList<Polyline>) {
        this.markers = markers
        this.polylines = polylines
    }

    /**
     * Saves the edited route.
     * @param oldRouteName name of the route before editing
     * @param routeName name of the route after editing
     * @throws IllegalArgumentException if the route has an illegal name, or it has less than 2 points
     */
    fun onRouteEdit(oldRouteName: String, routeName: String) {
        // TODO: only send to server if route was edited
        val points = markers.map {
            Point.from(it)
        }
        val route = Route(routeName, points)
        RouteUtils.checkRoute(route)
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                preferences[Constants.DATA_STORE_USER_NAME]
            }.collect { userName ->
                _routeEditRes.value = service.editRoute(userName!!, oldRouteName, route)
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
        MapUtils.onSingleTap(
            newMarker, markerType, markerTitle, p, markerIcon, setMarkerIcon, overlays, markers, polylines
        )
        markerTitle = ""
    }

    fun onMarkerDragEnd(marker: Marker) =
        MarkerUtils.onMarkerDragEnd(marker, markers.map {it.marker} as ArrayList<Marker>, polylines)

    fun onMarkerDragStart(marker: Marker) =
        MarkerUtils.onMarkerDragStart(marker, markers.map {it.marker} as ArrayList<Marker>, polylines)

    fun onDelete(
        marker: Marker,
        markerIcon: Drawable
    ): Boolean = MarkerUtils.onDelete(marker, markerIcon, markers, polylines)
}