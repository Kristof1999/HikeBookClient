package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class WeatherResponse(
    @Json(name = "code")
    val code: String?,
    @Json(name = "message")
    val message: Int?,
    @Json(name = "cnt")
    val cnt: Int?,
    @Json(name = "list")
    val list: List<ListResponse>?,
    @Json(name = "city")
    val city: City?
)
