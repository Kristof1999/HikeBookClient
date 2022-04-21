package hu.kristof.nagy.hikebookclient.viewModel.grouphike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.checkAndHandleRouteLoad
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to load the chosen group hike's route,
 * list participants, handle joining/leaving the group hike,
 * and helps to add the group hike's route to the user's map.
 */
@HiltViewModel
class GroupHikeDetailViewModel @Inject constructor(
    private val groupHikeRepository: GroupHikeRepository,
    private val userRouteRepository: UserRouteRepository
) : ViewModel() {
    private var _route = MutableLiveData<ResponseResult<Route>>()
    val route: LiveData<ResponseResult<Route>>
        get() = _route

    private var _participants = MutableLiveData<ResponseResult<List<String>>>()
    val participants: LiveData<ResponseResult<List<String>>>
        get() = _participants

    private var _generalConnectRes = MutableLiveData<ResponseResult<Boolean>>()
    val generalConnectRes: LiveData<ResponseResult<Boolean>>
        get() = _generalConnectRes

    private var _addToMyMapRes = MutableLiveData<ResponseResult<Boolean>>()
    val addToMyMapRes: LiveData<ResponseResult<Boolean>>
        get() = _addToMyMapRes

    var addToMyMapFinished = true

    fun loadRoute(groupHikeName: String) {
        viewModelScope.launch {
            _route.value = groupHikeRepository.loadRoute(groupHikeName)
        }
    }

    fun listParticipants(groupHikeName: String) {
        viewModelScope.launch {
            _participants.value = groupHikeRepository.listParticipants(groupHikeName)
        }
    }

    fun generalConnect(groupHikeName: String, isConnectedPage: Boolean, dateTime: DateTime) {
        viewModelScope.launch {
            groupHikeRepository.generalConnectForLoggedInUser(groupHikeName, isConnectedPage, dateTime).collect {
                _generalConnectRes.value = it
            }
        }
    }

    /**
     * Checks if the route has loaded.
     * If it has not, then it throws the appropriate exceptions.
     * If it has, then it calls the data layer to create
     * the loaded route for the logged in user,
     * and notifies the view layer of the result.
     * @throws IllegalStateException if the route has not loaded yet
     */
    fun addToMyMap() {
        if (checkAndHandleRouteLoad(_route.value!!)) {
            viewModelScope.launch {
                addToMyMapFinished = false
                val route = _route.value!!.successResult!!
                userRouteRepository
                    .createUserRouteForLoggedInUser(route.routeName, route.points, route.description)
                    .collect {
                        _addToMyMapRes.value = it
                    }
            }
        }
    }
}