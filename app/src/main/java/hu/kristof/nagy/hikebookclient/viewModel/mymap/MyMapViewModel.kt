package hu.kristof.nagy.hikebookclient.viewModel.mymap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.RouteRepository
import hu.kristof.nagy.hikebookclient.model.Route
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMapViewModel @Inject constructor(
    private val repository: RouteRepository
    ) : ViewModel() {

    private var _routes = MutableLiveData<List<Route>>()
    /**
     * List of the logged in user's routes.
     */
    val routes: LiveData<List<Route>>
        get() = _routes

    /**
     * Variable indicating that the deletion has finished.
     */
    var deleteFinished = true

    private var _deleteRes = MutableLiveData<Boolean>()
    /**
     * Result of deletion attempt.
     */
    val deleteRes: LiveData<Boolean>
        get() = _deleteRes

    fun loadRoutesForLoggedInUser() {
        viewModelScope.launch {
            repository.loadRoutesForLoggedInUser()
                .collect{ routes ->
                    _routes.value = routes
            }
        }
    }

    fun deleteRoute(routeName: String) {
        deleteFinished = false
        viewModelScope.launch {
            repository.deleteRoute(routeName)
                .collect { res ->
                    _deleteRes.value = res
                }
        }
    }

    fun getRoute(routeName: String): Route {
        return _routes.value!!.filter { route ->
            route.routeName == routeName
        }[0]
    }
}