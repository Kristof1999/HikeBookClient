package hu.kristof.nagy.hikebookclient.viewModel.mymap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.RouteUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@HiltViewModel
class RouteEditViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository
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

    /**
     * Saves the edited route.
     * @param oldRouteName name of the route before editing
     * @param routeName name of the route after editing
     * @throws IllegalArgumentException if the edited route has an illegal name, or it has less than 2 points
     */
    fun onRouteEdit(oldRouteName: String, routeName: String, hikeDescription: String) {
        // TODO: only send to server if route was edited
        val points = markers.map {
            Point.from(it)
        }
        RouteUtils.checkRoute(routeName, points)
        viewModelScope.launch {
                userRepository.editUserRoute(
                    oldRouteName, routeName, points, hikeDescription
                ).collect { res ->
                    _routeEditRes.value = res
                }
        }
    }
}