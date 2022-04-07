package hu.kristof.nagy.hikebookclient.viewModel.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.EditedUserRoute
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.UserRoute
import hu.kristof.nagy.hikebookclient.util.RouteUtils
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@HiltViewModel
class RouteEditViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository // TODO: change name
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

    // TODO: update javadoc
    /**
     * Saves the edited route.
     * @param oldRouteName name of the route before editing
     * @param routeName name of the route after editing
     * @throws IllegalArgumentException if the edited route has an illegal name, or it has less than 2 points
     */
    fun onRouteEdit(
        oldUserRoute: UserRoute,
        routeName: String,
        hikeDescription: String) {
        val points = markers.map {
            Point.from(it)
        }
        RouteUtils.checkRoute(routeName, points)
        val newUserRoute = UserRoute(oldUserRoute.userName, routeName, points, hikeDescription)
        val editedUserRoute = EditedUserRoute(newUserRoute, oldUserRoute)
        viewModelScope.launch {
            _routeEditRes.value = userRepository.editUserRoute(editedUserRoute)
        }
    }
}