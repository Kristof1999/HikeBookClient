package hu.kristof.nagy.hikebookclient.viewModel.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.GroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.routes.*
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@HiltViewModel
class RouteEditViewModel @Inject constructor(
    private val userRouteRepository: UserRouteRepository,
    private val groupRouteRepository: GroupRouteRepository
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

        when (oldRoute) {
            is UserRoute -> onUserRouteEdit(oldRoute, routeName, hikeDescription, points)
            is GroupRoute -> onGroupRouteEdit(oldRoute, routeName, hikeDescription, points)
        }
    }

    private fun onUserRouteEdit(
        oldUserRoute: UserRoute,
        routeName: String,
        hikeDescription: String,
        points: List<Point>
    ) {
        val newRoute = UserRoute(oldUserRoute.userName, routeName, points, hikeDescription)
        val editedUserRoute = EditedUserRoute(newRoute, oldUserRoute)

        viewModelScope.launch {
            _routeEditRes.value = userRouteRepository.editUserRoute(editedUserRoute)
        }
    }

    private fun onGroupRouteEdit(
        oldGroupRoute: GroupRoute,
        routeName: String,
        hikeDescription: String,
        points: List<Point>
    ) {
        val newRoute = GroupRoute(oldGroupRoute.groupName, routeName, points, hikeDescription)
        val editedGroupRoute = EditedGroupRoute(newRoute, oldGroupRoute)

        viewModelScope.launch {
            _routeEditRes.value = groupRouteRepository.editGroupRoute(editedGroupRoute)
        }
    }
}