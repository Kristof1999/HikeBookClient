package hu.kristof.nagy.hikebookclient.viewModel.hike

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HikeViewModel @Inject constructor(
    private val service: Service
) : ViewModel() {
    fun computeAndUpdateAvgSpeed(route: Route, startTime: Long, finishTime: Long) {
        val distance: Double = route.toPolyline().distance - 2 * Constants.GEOFENCE_RADIUS_IN_METERS
        val timeInMillis: Long = finishTime - startTime
        val millisecondsInHour: Float = Calendar.MILLISECONDS_IN_DAY / 24f
        val timeInHour: Float = timeInMillis / ( millisecondsInHour )
        val avgSpeed: Double = distance / timeInHour

        if (avgSpeed < 0) {
            Log.e("HikeViewModel", "avgSpeed is negative: $avgSpeed")
            return
        }

        viewModelScope.launch {
            service.updateAvgSpeed(route.ownerName, avgSpeed)
        }
    }
}