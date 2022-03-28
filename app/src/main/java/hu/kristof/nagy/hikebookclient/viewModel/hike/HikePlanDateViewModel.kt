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

    fun forecast(points: List<Point>, date: String) {
        viewModelScope.launch {
            var res = ""

            val responseStart = repository.forecast(
                points.first().latitude, points.first().longitude,
            )
            val idxStart = findDate(date, responseStart)
            res += "Túra kezdetén:\n\n" + mapResponse(responseStart, idxStart)

            val pointsMiddle = points.size/2
            val responseMiddle = repository.forecast(
                points[pointsMiddle].latitude, points[pointsMiddle].longitude,
            )
            // could be a 6h increment if we made the
            // first call at 9:59 and the second at
            // 10:01, because see api specifications
            val idxMiddle = idxStart + 1 // 3h increment
            res += "Túra felénél:\n\n" + mapResponse(responseMiddle, idxMiddle)

            val responseEnd = repository.forecast(
                points.last().latitude, points.last().longitude,
            )
            val idxEnd = idxMiddle + 1 // 3h increment
            res += "Túra végénél:\n\n" + mapResponse(responseEnd, idxEnd)

            _forecastRes.value = res
        }
    }

    private fun findDate(date: String, response: WeatherResponse): Int {
        for (i in response?.list?.indices!!) {
            if (response?.list?.get(i)?.dtTxt?.substring(0, 10) == date)
                return i
        }
        throw IndexOutOfBoundsException("$date cannot be found in the weather response.")
    }

    private fun mapResponse(response: WeatherResponse, idx: Int): String {
        val cityName = response.city?.name
        val dateTime = response.list?.get(idx)?.dtTxt
        val temp = response.list?.get(idx)?.main?.feelsLike
        val description = response.list?.get(idx)?.weather?.get(0)?.description
        val rainPrecent = response.list?.get(idx)?.pop
        val windSpeed = response.list?.get(idx)?.wind?.speed
        val visibilityMetres = response.list?.get(idx)?.visibility
        return "$cityName - $dateTime\n" +
                    "Várható hőmérséklet: $temp°C\n" +
                    "Várható időjárás: $description\n" +
                    "Csapadék valószínűség: ${rainPrecent?.times(100)}%\n" +
                    "Szélerő: $windSpeed, láthatóság: ${visibilityMetres}m\n\n"
    }
}