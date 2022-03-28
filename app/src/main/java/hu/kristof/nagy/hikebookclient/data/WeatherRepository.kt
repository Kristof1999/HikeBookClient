package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.data.network.WeatherService
import hu.kristof.nagy.hikebookclient.model.weather.WeatherResponse
import hu.kristof.nagy.hikebookclient.util.Constants
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    val service: WeatherService
) {
    suspend fun forecast(lat: Double, lon: Double): WeatherResponse {
        return service.forecast(
            lat, lon, Constants.METRIC_UNIT,
            Constants.LANGUAGE, BuildConfig.WEATHER_API_KEY
        )
    }
}