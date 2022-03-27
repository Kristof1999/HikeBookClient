package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.WeatherRepository
import hu.kristof.nagy.hikebookclient.model.Point
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HikePlanDateViewModel @Inject constructor(
    private val repository: WeatherRepository
    ) : ViewModel() {

    private var _forecastRes = MutableLiveData<String>()
    val forecastRes: LiveData<String>
        get() = _forecastRes

    fun forecast(points: List<Point>) {
        viewModelScope.launch {
            _forecastRes.value = repository.forecast(
                points[0].latitude, points[0].longitude,
            ).toString()
        }
    }
}