package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.GroupRouteRepository
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.GroupRoute
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to load the given group's routes,
 * and helps with adding routes from the user's map to the group's map.
 */
@HiltViewModel
class GroupsDetailMapViewModel @Inject constructor(
    private val groupRouteRepository: GroupRouteRepository
    ) : ViewModel() {
    private val _routes = MutableLiveData<ResponseResult<List<GroupRoute>>>()
    val routes: LiveData<ResponseResult<List<GroupRoute>>>
        get() = _routes

    private val _addFromMyMapRes = MutableLiveData<ResponseResult<Boolean>>()
    val addFromMyMapRes: LiveData<ResponseResult<Boolean>>
        get() = _addFromMyMapRes

    var addFromMyMapFinished = true

    /**
     * Calls the data layer to load the given group's routes,
     * and notifies the view layer of the result.
     */
    fun loadRoutesOfGroup(groupName: String) {
        viewModelScope.launch {
            _routes.value = groupRouteRepository.loadGroupRoutes(groupName)
        }
    }

    /**
     * Forwards the call to the data layer, and
     * and notifies the view layer of the result
     */
    fun onAddFromMyMap(route: Route, groupName: String) {
        addFromMyMapFinished = false
        viewModelScope.launch {
            _addFromMyMapRes.value = groupRouteRepository.createGroupRoute(
                groupName, route.routeName, route.points, route.description
            )
        }
    }

    /**
     * Gives back the route with the given route name.
     */
    fun getRoute(routeName: String): Route {
        return _routes.value!!.successResult!!.filter { route ->
            route.routeName == routeName
        }[0]
    }
}