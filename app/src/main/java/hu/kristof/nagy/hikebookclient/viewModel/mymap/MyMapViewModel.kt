package hu.kristof.nagy.hikebookclient.viewModel.mymap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.UserRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMapViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository
    ) : ViewModel() {

    private var _routes = MutableLiveData<Result<List<UserRoute>>>()
    /**
     * List of the logged in user's routes.
     */
    val routes: LiveData<Result<List<UserRoute>>>
        get() = _routes

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

    fun loadRoutesForLoggedInUser() {
        viewModelScope.launch {
            userRepository.loadUserRoutes()
                .collect{ routes ->
                    _routes.value = routes
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

    fun getRoute(routeName: String): UserRoute {
        return _routes.value!!.getOrNull()!!.filter { route ->
            route.routeName == routeName
        }[0]
    }
}