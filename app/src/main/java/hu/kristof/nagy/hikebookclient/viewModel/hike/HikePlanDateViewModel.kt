package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.WeatherRepository
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.weather.WeatherResponse
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
            var res = ""

            val responseStart = repository.forecast(
                points.first().latitude, points.first().longitude,
            )
            val idxStart = 0
            res += "Túra kezdetén:\n" + mapResponse(responseStart, idxStart)

            val pointsMiddle = points.size/2
            val responseMiddle = repository.forecast(
                points[pointsMiddle].latitude, points[pointsMiddle].longitude,
            )
            val idxMiddle = 1
            res += "Túra felénél:\n" + mapResponse(responseMiddle, idxMiddle)

            val responseEnd = repository.forecast(
                points.last().latitude, points.last().longitude,
            )
            val idxEnd = 2
            res += "Túra végénél:\n" + mapResponse(responseEnd, idxEnd)

            _forecastRes.value = res
        }
    }

    private fun mapResponse(response: WeatherResponse, idx: Int): String {
        val cityName = response.city?.name
        val dateTime = response.list?.get(idx)?.dtTxt
        val temp = response.list?.get(idx)?.main?.feelsLike
        val description = response.list?.get(idx)?.weather?.get(0)?.description
        val cloudPrecent = response.list?.get(idx)?.clouds?.all
        val rainPrecent = response.list?.get(idx)?.pop
        val rainVolume = response.list?.get(idx)?.rain?.vol
        val windSpeed = response.list?.get(idx)?.wind?.speed
        val visibilityMetres = response.list?.get(idx)?.visibility
        return "Város név: $cityName, dátum és idő: $dateTime\n" +
                    "Várható hőmérséklet: $temp\n°C" +
                    "Várható időjárás: $description\n" +
                    "Felhő valószínűség: $cloudPrecent%\n" +
                    "Csapadék valószínűség: ${rainPrecent?.times(100)}%\n" +
                    "Várható csapadék az elmúlt 3 órában: $rainVolume\n" +
                    "Szélerő: $windSpeed, Láthatóság: ${visibilityMetres}m\n"
    }
}