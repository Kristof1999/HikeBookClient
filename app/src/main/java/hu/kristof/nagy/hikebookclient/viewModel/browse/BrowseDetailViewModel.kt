package hu.kristof.nagy.hikebookclient.viewModel.browse

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import hu.kristof.nagy.hikebookclient.util.handleIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.handleRouteLoad
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to load the chosen user's chosen route,
 * and helps with adding the chosen route to the logged in user's map.
 */
@HiltViewModel
class BrowseDetailViewModel @Inject constructor(
    private val userRouteRepository: UserRouteRepository
    ) : ViewModel() {
    private val _route = MutableLiveData<ServerResponseResult<UserRoute>>()
    val route: LiveData<ServerResponseResult<UserRoute>>
        get() = _route

    private val _addRes = MutableLiveData<ResponseResult<Boolean>>()
    val addRes: LiveData<ResponseResult<Boolean>>
        get() = _addRes

    fun loadDetails(userName: String, routeName: String) {
        viewModelScope.launch {
            _route.value = userRouteRepository.loadUserRouteOfUser(userName, routeName)
        }
    }

    // TODO: update -> remove throws
    /**
     * Before calling the data layer to
     * create the chosen route for the logged in user,
     * it checks whether the chosen route has been loaded.
     * If the route has been loaded, then it calls the data layer,
     * and notifies the view layer of the result.
     * @throws IllegalStateException if the route has not loaded yet
     */
    fun addToMyMap(routeName: java.lang.String, context: Context) {
        viewModelScope.launch {
           handleIllegalStateAndArgument(_addRes) {
               handleOffline(_addRes, context) {
                   handleRouteLoad(_route.value!!, _addRes)
                   val points = _route.value!!.successResult!!.points
                   val description = _route.value!!.successResult!!.description
                   userRouteRepository
                       .createUserRouteForLoggedInUser(routeName as kotlin.String, points, description)
                       .collect {
                           _addRes.value = it
                       }
               }
           }
        }
    }
}