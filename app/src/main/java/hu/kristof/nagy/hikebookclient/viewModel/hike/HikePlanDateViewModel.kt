package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.data.network.WeatherService
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HikePlanDateViewModel @Inject constructor(
    private val service: WeatherService
    ) : ViewModel() {

    private var _forecastRes = MutableLiveData<String>()
    val forecastRes: LiveData<String>
        get() = _forecastRes

    fun forecast(points: List<Point>) {
        // TODO: only show 2 timestamps from the specified time
        viewModelScope.launch {
            _forecastRes.value = service.forecast(
                points[0].latitude, points[0].longitude,
                Constants.METRIC_UNIT, Constants.FORECAST_CNT,
                BuildConfig.WEATHER_API_KEY
            ).toString()
        }
    }
}