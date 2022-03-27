package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class City(
    @Json(name = "id")
    val id: Long?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "coord")
    val coord: CityCoord?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "population")
    val population: Int?,
    @Json(name = "timezone")
    val timezone: Int?,
    @Json(name = "sunrise")
    val sunrise: Long?,
    @Json(name = "sunset")
    val sunset: Long?
)