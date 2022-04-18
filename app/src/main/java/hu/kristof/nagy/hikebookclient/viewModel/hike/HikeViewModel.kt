package hu.kristof.nagy.hikebookclient.viewModel.hike

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import hu.kristof.nagy.hikebookclient.util.Constants
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
    private var _route = MutableLiveData<Result<UserRoute>>()
    val route: LiveData<Result<UserRoute>>
        get() = _route

    fun loadUserRoute(routeName: String) {
        viewModelScope.launch {
            userRouteRepository.loadUserRouteOfLoggedInUser(routeName).collect {
                _route.value = it
            }
        }
    }

    fun computeAndUpdateAvgSpeed(route: UserRoute, startTime: Long, finishTime: Long) {
        val distance: Double = route.getDistance() - 2 * Constants.GEOFENCE_RADIUS_IN_METERS
        val timeInMillis: Long = finishTime - startTime
        val millisecondsInHour: Float = Calendar.MILLISECONDS_IN_DAY / 24f
        val timeInHour: Float = timeInMillis / ( millisecondsInHour )
        val avgSpeed: Double = distance / timeInHour

        if (avgSpeed < 0) {
            Log.e("HikeViewModel", "avgSpeed is negative: $avgSpeed")
            return
        }

        viewModelScope.launch {
            service.updateAvgSpeed(route.userName, avgSpeed)
        }
    }
}