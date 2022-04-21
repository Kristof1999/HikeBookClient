package hu.kristof.nagy.hikebookclient.viewModel.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import hu.kristof.nagy.hikebookclient.util.checkAndHandleRouteLoad
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
    private var _route = MutableLiveData<Result<UserRoute>>()
    val route: LiveData<Result<UserRoute>>
        get() = _route

    private var _addRes = MutableLiveData<ResponseResult<Boolean>>()
    val addRes: LiveData<ResponseResult<Boolean>>
        get() = _addRes

    fun loadDetails(userName: String, routeName: String) {
        viewModelScope.launch {
            _route.value = userRouteRepository.loadUserRouteOfUser(userName, routeName)
        }
    }

    /**
     * Before calling the data layer to
     * create the chosen route for the logged in user,
     * it checks whether the chosen route has been loaded.
     * If the route has been loaded, then it calls the data layer,
     * and notifies the view layer of the result.
     * @throws IllegalStateException if the route has not loaded yet
     */
    fun addToMyMap(routeName: String) {
        viewModelScope.launch {
            if (checkAndHandleRouteLoad(_route.value!!)) {
                val points = _route.value!!.getOrNull()!!.points
                val description = _route.value!!.getOrNull()!!.description
                userRouteRepository
                    .createUserRouteForLoggedInUser(routeName, points, description)
                    .collect {
                    _addRes.value = it
                }
            }
        }
    }
}