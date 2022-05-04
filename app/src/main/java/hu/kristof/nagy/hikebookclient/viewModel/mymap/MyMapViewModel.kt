package hu.kristof.nagy.hikebookclient.viewModel.mymap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.repository.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to load all the routes a user has.
 */
@HiltViewModel
class MyMapViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository
    ) : ViewModel() {

    private val _routes = MutableLiveData<ServerResponseResult<List<Route.UserRoute>>>()
    val routes: LiveData<ServerResponseResult<List<Route.UserRoute>>>
        get() = _routes

    fun loadRoutesForLoggedInUser() {
        viewModelScope.launch {
            userRepository.loadUserRoutesOfLoggedInUser()
                .collect{ routes ->
                    _routes.value = routes
            }
        }
    }
}