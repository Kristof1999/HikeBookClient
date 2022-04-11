package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.GroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsDetailMapViewModel @Inject constructor(
    private val groupRouteRepository: GroupRouteRepository,
    private val userRouteRepository: UserRouteRepository
    ) : ViewModel() {
    private var _routes = MutableLiveData<Result<List<Route>>>()
    val routes: LiveData<Result<List<Route>>>
        get() = _routes

    private var _addFromMyMapRes = MutableLiveData<Result<Boolean>>()
    val addFromMyMapRes: LiveData<Result<Boolean>>
        get() = _addFromMyMapRes

    private var _deleteRes = MutableLiveData<Result<Boolean>>()
    val deleteRes: LiveData<Result<Boolean>>
        get() = _deleteRes

    private var _addToMyMapRes = MutableLiveData<Result<Boolean>>()
    val addToMyMapRes: LiveData<Result<Boolean>>
        get() = _addToMyMapRes

    fun loadRoutesOfGroup(groupName: String) {
        viewModelScope.launch {
            _routes.value = groupRouteRepository.loadRoutes(groupName)
        }
    }

    fun onAddFromMyMap(route: Route, groupName: String) {
        viewModelScope.launch {
            _addFromMyMapRes.value = groupRouteRepository.createRoute(
                groupName, route.routeName, route.points, route.description
            )
            // refresh if successful
            if (_addFromMyMapRes.value?.isSuccess!!)
                loadRoutesOfGroup(groupName)
        }
    }

    fun onAddToMyMap(routeName: String) {
        viewModelScope.launch {
            val route = getRoute(routeName)
            userRouteRepository.createUserRoute(
                routeName, route.points, route.description
            ).collect { res ->
                _addToMyMapRes.value = res
            }
        }
    }

    fun onDelete(groupName: String, routeName: String) {
        viewModelScope.launch {
            _deleteRes.value = groupRouteRepository.deleteRoute(groupName, routeName)
        }
    }

    fun getRoute(routeName: String): Route {
        return _routes.value!!.getOrNull()!!.filter { route ->
            route.routeName == routeName
        }[0]
    }
}