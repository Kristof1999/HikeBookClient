package hu.kristof.nagy.hikebookclient.viewModel.mymap

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.checkAndHandleRouteLoad
import hu.kristof.nagy.hikebookclient.util.handleOffline
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * A ViewModel that helps to load the chosen route,
 * and helps with deleting the chosen route,
 * and helps with creating a group hike.
 */
@HiltViewModel
class MyMapDetailViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository,
    private val groupHikeRepository: GroupHikeRepository
) : ViewModel() {
    private val _route = MutableLiveData<ServerResponseResult<Route.UserRoute>>()
    val route: LiveData<ServerResponseResult<Route.UserRoute>>
        get() = _route

    var deleteFinished = true

    private val _deleteRes = MutableLiveData<ResponseResult<Boolean>>()
    val deleteRes: LiveData<ResponseResult<Boolean>>
        get() = _deleteRes

    private val _groupHikeCreateRes = MutableLiveData<ServerResponseResult<Boolean>>()
    val groupHikeCreateRes: LiveData<ServerResponseResult<Boolean>>
        get() = _groupHikeCreateRes

    var groupHikeCreationFinished = true

    fun loadUserRoute(routeName: String) {
        viewModelScope.launch {
            userRepository.loadUserRouteOfLoggedInUser(routeName).collect {
                _route.value = it
            }
        }
    }

    fun deleteRoute(routeName: java.lang.String, context: Context) {
        deleteFinished = false
        viewModelScope.launch {
            handleOffline(_deleteRes, context) {
                userRepository.deleteUserRouteOfLoggedInUser(routeName as kotlin.String)
                    .collect { res ->
                        _deleteRes.value = res
                    }
            }
        }
    }

    /**
     * Checks if the group hike name is not empty,
     * and does not contain a / symbol.
     * If the name is ok, then it checks if the route is loaded.
     * It it's loaded, then it calls the data layer to create the group hike,
     * and notifies the view layer of the result.
     * @throws IllegalStateException if the route has not loaded yet
     * @throws IllegalArgumentException if the name is not ok
     */
    fun createGroupHike(dateTime: Calendar, groupHikeName: String) {
        Route.GroupHikeRoute.checkGroupHikeName(groupHikeName)

        if (checkAndHandleRouteLoad(_route.value!!)) {
            viewModelScope.launch {
                val route = _route.value!!.successResult!!
                val groupHikeRoute = Route.GroupHikeRoute(
                    groupHikeName, route.routeName, route.points, route.description
                )
                groupHikeCreationFinished = false
                groupHikeRepository
                    .createGroupHikeForLoggedInUser(groupHikeName, dateTime, groupHikeRoute)
                    .collect {
                    _groupHikeCreateRes.value = it
                }
            }
        }
    }
}