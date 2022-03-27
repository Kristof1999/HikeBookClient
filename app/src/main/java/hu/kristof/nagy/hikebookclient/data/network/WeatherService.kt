// using: OpenWeather
// based on:
// https://openweathermap.org/forecast5#5days

package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast")
    suspend fun forecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unit: String,
        @Query("cnt") cnt: Int,
        @Query("appid") appid: String
    ): WeatherResponse
}