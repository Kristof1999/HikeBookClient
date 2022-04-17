package hu.kristof.nagy.hikebookclient.viewModel.mymap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import hu.kristof.nagy.hikebookclient.util.checkAndHandleRouteLoad
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
    private var _route = MutableLiveData<Result<UserRoute>>()
    val route: LiveData<Result<UserRoute>>
        get() = _route

    var deleteFinished = true

    private var _deleteRes = MutableLiveData<Result<Boolean>>()
    val deleteRes: LiveData<Result<Boolean>>
        get() = _deleteRes

    private var _groupHikeCreateRes = MutableLiveData<Result<Boolean>>()
    val groupHikeCreateRes: LiveData<Result<Boolean>>
        get() = _groupHikeCreateRes

    var groupHikeCreationFinished = true

    fun loadUserRoute(routeName: String) {
        viewModelScope.launch {
            userRepository.loadUserRouteOfLoggedInUser(routeName).collect {
                _route.value = it
            }
        }
    }

    fun deleteRoute(routeName: String) {
        deleteFinished = false
        viewModelScope.launch {
            userRepository.deleteUserRoute(routeName)
                .collect { res ->
                    _deleteRes.value = res
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
        if (groupHikeName.isEmpty())
            throw IllegalArgumentException("A csoportos túra neve nem lehet üres.")
        if (groupHikeName.contains("/"))
            throw IllegalArgumentException("A csoportos túra név nem tartalmazhat / jelet.")

        if (checkAndHandleRouteLoad(_route.value!!)) {
            viewModelScope.launch {
                val route = _route.value!!.getOrNull()!!
                groupHikeCreationFinished = false
                groupHikeRepository.createGroupHike(groupHikeName, dateTime, route).collect {
                    _groupHikeCreateRes.value = it
                }
            }
        }
    }
}