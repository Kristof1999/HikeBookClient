package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.weather.WeatherResponse

interface IWeatherRepository {
    suspend fun forecast(lat: Double, lon: Double): WeatherResponse
}