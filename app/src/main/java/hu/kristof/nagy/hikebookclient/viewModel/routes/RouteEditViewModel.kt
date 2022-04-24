package hu.kristof.nagy.hikebookclient.viewModel.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.GroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.routes.*
import hu.kristof.nagy.hikebookclient.util.checkAndHandleRouteLoad
import hu.kristof.nagy.hikebookclient.view.routes.RouteEditFragmentArgs
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

/**
 * A RouteViewModel that helps to load the route chosen for editing,
 * and helps to save the changes made to either a user or a group route.
 */
@HiltViewModel
class RouteEditViewModel @Inject constructor(
    private val userRouteRepository: UserRouteRepository,
    private val groupRouteRepository: GroupRouteRepository
    ) : RouteViewModel() {
    private val _route = MutableLiveData<ResponseResult<Route>>()
    val route: LiveData<ResponseResult<Route>>
        get() = _route

    private val _routeEditRes = MutableLiveData<ResponseResult<Boolean>>()
    val routeEditRes: LiveData<ResponseResult<Boolean>>
        get() = _routeEditRes

    fun setup(markers: MutableList<MyMarker>, polylines: MutableList<Polyline>) {
        _markers.addAll(markers)
        _polylines.addAll(polylines)
    }

    fun loadRoute(args: RouteEditFragmentArgs) {
        when (args.routeType) {
            RouteType.USER -> loadUserRoute(args.routeName)
            RouteType.GROUP -> loadGroupRoute(args.routeName, args.groupName!!)
        }
    }

    private fun loadGroupRoute(routeName: String, groupName: String) {
        viewModelScope.launch {
            _route.value = groupRouteRepository.loadGroupRoute(groupName, routeName) as ResponseResult<Route>
        }
    }

    private fun loadUserRoute(routeName: String) {
        viewModelScope.launch {
            userRouteRepository.loadUserRouteOfLoggedInUser(routeName).collect {
                _route.value = it as ResponseResult<Route>
            }
        }
    }

    /**
     * Checks if the route has loaded yet.
     * If the route has loaded, then it calls the data layer
     * to save the changes, and notifies the view layer of the result.
     * @throws IllegalStateException if the route has not loaded yet
     */
    fun onRouteEdit(
        routeName: String,
        hikeDescription: String
    ) {
        if (checkAndHandleRouteLoad(_route.value!!)) {
            val oldRoute = _route.value!!.successResult!!

            val points = _markers.map {
                Point.from(it)
            }

            when (oldRoute) {
                is UserRoute -> onUserRouteEdit(oldRoute, routeName, hikeDescription, points)
                is GroupRoute -> onGroupRouteEdit(oldRoute, routeName, hikeDescription, points)
                else -> throw IllegalArgumentException("Unkown route type: $oldRoute")
            }
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