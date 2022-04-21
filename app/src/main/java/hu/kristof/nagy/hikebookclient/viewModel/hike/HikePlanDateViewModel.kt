package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.IWeatherRepository
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.model.weather.WeatherResponse
import hu.kristof.nagy.hikebookclient.util.checkAndHandleRouteLoad
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to forecast the weather
 * for the chosen date and time, and
 * helps to load the route chosen for hiking.
 */
@HiltViewModel
class HikePlanDateViewModel @Inject constructor(
    private val repository: IWeatherRepository,
    private val userRouteRepository: IUserRouteRepository
    ) : ViewModel() {

    private var _forecastRes = MutableLiveData<String>()
    val forecastRes: LiveData<String>
        get() = _forecastRes

    private var _route = MutableLiveData<Result<Route>>()
    val route: LiveData<Result<Route>>
        get() = _route

    fun loadRoute(routeName: String) {
        viewModelScope.launch {
            userRouteRepository.loadUserRouteOfLoggedInUser(routeName).collect {
                _route.value = it
            }
        }
    }

    /**
     * Checks whether the route has loaded yet.
     * If it has, then it calls the data layer 3 times to
     * get weather forecast for the start, middle, and end
     * of the hike route with 3 hour intervals.
     * It then notifies the view layer of the result.
     * @throws IllegalStateException if the route has not loaded yet
     */
    fun forecast(date: String, hour: Int) {
        if (checkAndHandleRouteLoad(_route.value!!)) {
            val points = _route.value!!.getOrNull()!!.points

            viewModelScope.launch {
                var res = ""

                val dateStartIdx = repository.forecast(
                    points.first().latitude, points.first().longitude,
                ).let { startPointResponse ->
                    val dateStartIdx = DateHourIdxFinder.findDateIdx(date, startPointResponse)
                    val idxStart = DateHourIdxFinder
                        .findHourIdx(hour, startPointResponse, dateStartIdx)
                    res += "Túra kezdetén:\n\n" + mapResponse(startPointResponse, idxStart)
                    dateStartIdx
                }

                val pointsMiddle = points.size/2
                repository.forecast(
                    points[pointsMiddle].latitude, points[pointsMiddle].longitude,
                ).let { middlePointResponse ->
                    val idxMiddle = DateHourIdxFinder
                        .findHourIdx(hour + 3, middlePointResponse, dateStartIdx)
                    res += "Túra felénél:\n\n" + mapResponse(middlePointResponse, idxMiddle)
                    idxMiddle
                }

                repository.forecast(
                    points.last().latitude, points.last().longitude,
                ).let { endPointResponse ->
                    val idxEnd = DateHourIdxFinder
                        .findHourIdx(hour + 6, endPointResponse, dateStartIdx)
                    res += "Túra végénél:\n\n" + mapResponse(endPointResponse, idxEnd)
                }

                _forecastRes.value = res
            }
        }
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