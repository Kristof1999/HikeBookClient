package hu.kristof.nagy.hikebookclient.viewModel.routes

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.GroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.*
import hu.kristof.nagy.hikebookclient.model.routes.EditedRoute
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.handleIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.handleRouteLoad
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
    private val _route = MutableLiveData<ServerResponseResult<Route>>()
    val route: LiveData<ServerResponseResult<Route>>
        get() = _route

    private val _routeEditRes = MutableLiveData<ResponseResult<Boolean>>()
    val routeEditRes: LiveData<ResponseResult<Boolean>>
        get() = _routeEditRes

    fun setup(markers: MutableList<MyMarker>, polylines: MutableList<Polyline>) {
        myMarkers.addAll(markers)
        myPolylines.addAll(polylines.map {
            MyPolyline.from(it)
        })
    }

    fun loadRoute(args: RouteEditFragmentArgs) {
        when (args.routeType) {
            RouteType.USER -> loadUserRoute(args.routeName)
            RouteType.GROUP -> loadGroupRoute(args.routeName, args.groupName!!)
        }
    }

    private fun loadGroupRoute(routeName: String, groupName: String) {
        viewModelScope.launch {
            _route.value = groupRouteRepository.loadGroupRoute(groupName, routeName) as ServerResponseResult<Route>
        }
    }

    private fun loadUserRoute(routeName: String) {
        viewModelScope.launch {
            userRouteRepository.loadUserRouteOfLoggedInUser(routeName).collect {
                _route.value = it as ServerResponseResult<Route>
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
        context: Context
    ) {
        handleRouteLoad(_route.value, _routeEditRes) {
            viewModelScope.launch {
                handleOffline(_routeEditRes, context) {
                    handleIllegalStateAndArgument(_routeEditRes) {
                        val oldRoute = _route.value!!.successResult!!

                        val points = myMarkers.map {
                            Point.from(it)
                        }

                        when (oldRoute) {
                            is Route.UserRoute -> onUserRouteEdit(
                                oldRoute, routeName, hikeDescription, points
                            )
                            is Route.GroupRoute -> onGroupRouteEdit(
                                oldRoute, routeName, hikeDescription, points
                            )
                            is Route.GroupHikeRoute ->
                                throw IllegalArgumentException("Illegal route: $oldRoute")
                        }
                    }
                }
            }
        }
    }

    private suspend fun onUserRouteEdit(
        oldUserRoute: Route.UserRoute,
        routeName: String,
        hikeDescription: String,
        points: List<Point>
    ) {
        val newRoute = Route.UserRoute(oldUserRoute.userName, routeName, points, hikeDescription)
        val editedUserRoute = EditedRoute.EditedUserRoute(newRoute, oldUserRoute)

        _routeEditRes.value = userRouteRepository.editUserRoute(editedUserRoute)
    }

    private suspend fun onGroupRouteEdit(
        oldGroupRoute: Route.GroupRoute,
        routeName: String,
        hikeDescription: String,
        points: List<Point>
    ) {
        val newRoute = Route.GroupRoute(oldGroupRoute.groupName, routeName, points, hikeDescription)
        val editedGroupRoute = EditedRoute.EditedGroupRoute(newRoute, oldGroupRoute)

        _routeEditRes.value = groupRouteRepository.editGroupRoute(editedGroupRoute)
    }
}