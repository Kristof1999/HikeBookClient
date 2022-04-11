package hu.kristof.nagy.hikebookclient.viewModel.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.RouteRepository
import hu.kristof.nagy.hikebookclient.model.routes.EditedRoute
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@HiltViewModel
class RouteEditViewModel @Inject constructor(
    private val routeRepository: RouteRepository
    ) : RouteViewModel() {
    override lateinit var markers: ArrayList<MyMarker>
    override lateinit var polylines: ArrayList<Polyline>

    private var _routeEditRes = MutableLiveData<Result<Boolean>>()
    /**
     * Result of route edit attempt.
     */
    val routeEditRes: LiveData<Result<Boolean>>
        get() = _routeEditRes

    fun setup(markers: ArrayList<MyMarker>, polylines: ArrayList<Polyline>) {
        this.markers = markers
        this.polylines = polylines
    }

    fun onRouteEdit(
        oldRoute: Route,
        routeName: String,
        hikeDescription: String
    ) {
        val points = markers.map {
            Point.from(it)
        }
        RouteUtils.checkRoute(routeName, points)
        val newRoute = Route(oldRoute.ownerName, oldRoute.routeType, routeName, points, hikeDescription)
        val editedRoute = EditedRoute(newRoute, oldRoute)

        viewModelScope.launch {
            _routeEditRes.value = routeRepository.editRoute(editedRoute)
        }
    }
}