package hu.kristof.nagy.hikebookclient.viewModel.hike

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.repository.IHikeRepository
import hu.kristof.nagy.hikebookclient.data.repository.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.checkAndHandleRouteLoad
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Helps to load the route chosen for hiking,
 * and helps with computing and updating the user's average speed.
 */
@HiltViewModel
class HikeViewModel @Inject constructor(
    private val hikeRepository: IHikeRepository,
    private val userRouteRepository: IUserRouteRepository
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
                hikeRepository.updateAvgSpeed(route.userName, avgSpeed)
            }
        }
    }

    fun isPointInCircle(point: GeoPoint, center: GeoPoint, radius: Int): Boolean {
        // based on: https://en.wikipedia.org/wiki/Great-circle_distance
        val x1 = point.latitude
        val x2 = center.latitude
        val y1 = point.longitude
        val y2 = center.longitude
        val dx = x2 - x1
        val dy = y2 - y1
        val centralAngle = 2 * asin(
            sqrt(
                hav(dx) + (1 - hav(dx) - hav(x1 + x2)) * hav(dy)
            )
        )
        val r = 6371.009 * 1000 // mean earth radius in meters
        val distance = r * centralAngle
        return distance <= radius*radius
    }

    private fun hav(x: Double): Double {
        return sin(x/2) * sin(x/2)
    }
}