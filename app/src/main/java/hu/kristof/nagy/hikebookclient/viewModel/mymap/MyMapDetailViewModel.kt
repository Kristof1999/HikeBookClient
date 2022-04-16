package hu.kristof.nagy.hikebookclient.viewModel.mymap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import hu.kristof.nagy.hikebookclient.util.routeLoaded
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MyMapDetailViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository,
    private val groupHikeRepository: GroupHikeRepository
) : ViewModel() {
    private var _route = MutableLiveData<Result<UserRoute>>()
    val route: LiveData<Result<UserRoute>>
        get() = _route

    /**
     * Variable indicating that the deletion has finished.
     */
    var deleteFinished = true

    private var _deleteRes = MutableLiveData<Result<Boolean>>()
    /**
     * Result of deletion attempt.
     */
    val deleteRes: LiveData<Result<Boolean>>
        get() = _deleteRes

    private var _groupHikeCreateRes = MutableLiveData<Result<Boolean>>()
    val groupHikeCreateRes: LiveData<Result<Boolean>>
        get() = _groupHikeCreateRes

    var groupHikeCreationFinished = true

    fun loadUserRoute(routeName: String) {
        viewModelScope.launch {
            userRepository.loadUserRoute(routeName).collect {
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

    fun createGroupHike(dateTime: Calendar, groupHikeName: String) {
        if (groupHikeName.isEmpty())
            throw IllegalArgumentException("A csoportos túra neve nem lehet üres.")
        if (groupHikeName.contains("/"))
            throw IllegalArgumentException("A csoportos túra név nem tartalmazhat / jelet.")

        if (routeLoaded(_route)) {
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