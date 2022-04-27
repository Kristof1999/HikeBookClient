package hu.kristof.nagy.hikebookclient.viewModel.hike

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.checkAndHandleRouteLoad
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Helps to load the route chosen for hiking,
 * and helps with computing and updating the user's average speed.
 */
@HiltViewModel
class HikeViewModel @Inject constructor(
    private val service: Service,
    private val userRouteRepository: UserRouteRepository
) : ViewModel() {
    private val _route = MutableLiveData<ServerResponseResult<Route.UserRoute>>()
    val route: LiveData<ServerResponseResult<Route.UserRoute>>
        get() = _route

    fun loadUserRoute(routeName: String) {
        viewModelScope.launch {
            userRouteRepository.loadUserRouteOfLoggedInUser(routeName).collect {
                _route.value = it
            }
        }
    }

    fun computeAndUpdateAvgSpeed(startTime: Long, finishTime: Long) {
        if (checkAndHandleRouteLoad(_route.value)) {
            val route = _route.value!!.successResult!!
            val avgSpeed = route.computeAvgSpeed(startTime, finishTime)

            if (avgSpeed < 0) {
                Log.e("HikeViewModel", "avgSpeed is negative: $avgSpeed")
                return
            }

            viewModelScope.launch {
                service.updateAvgSpeed(route.userName, avgSpeed)
            }
        }
    }
}