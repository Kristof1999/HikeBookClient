package hu.kristof.nagy.hikebookclient.viewModel.grouphike

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.repository.IGroupHikeRepository
import hu.kristof.nagy.hikebookclient.data.repository.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.handleIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.handleRouteLoad
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
    private val groupHikeRepository: IGroupHikeRepository,
    private val userRouteRepository: IUserRouteRepository
) : ViewModel() {
    private val _route = MutableLiveData<ServerResponseResult<Route.GroupHikeRoute>>()
    val route: LiveData<ServerResponseResult<Route.GroupHikeRoute>>
        get() = _route

    private val _participants = MutableLiveData<ServerResponseResult<List<String>>>()
    val participants: LiveData<ServerResponseResult<List<String>>>
        get() = _participants

    private val _generalConnectRes = MutableLiveData<ResponseResult<Boolean>>()
    val generalConnectRes: LiveData<ResponseResult<Boolean>>
        get() = _generalConnectRes

    private val _addToMyMapRes = MutableLiveData<ResponseResult<Boolean>>()
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

    fun generalConnect(
        groupHikeName: java.lang.String,
        isConnectedPage: java.lang.Boolean,
        dateTime: DateTime,
        context: Context
    ) {
        viewModelScope.launch {
            handleOffline(_generalConnectRes, context) {
                groupHikeRepository.generalConnectForLoggedInUser(
                    groupHikeName as kotlin.String,
                    isConnectedPage as kotlin.Boolean,
                    dateTime
                ).collect {
                    _generalConnectRes.value = it
                }
            }
        }
    }

    /**
     * Checks if the route has loaded.
     * If it has not, then it throws the appropriate exceptions.
     * If it has, then it calls the data layer to create
     * the loaded route for the logged in user,
     * and notifies the view layer of the result.
     */
    fun addToMyMap(context: Context) {
        handleRouteLoad(_route.value, _addToMyMapRes) {
            viewModelScope.launch {
                handleIllegalStateAndArgument(_addToMyMapRes) {
                    handleOffline(_addToMyMapRes, context) {
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
    }
}