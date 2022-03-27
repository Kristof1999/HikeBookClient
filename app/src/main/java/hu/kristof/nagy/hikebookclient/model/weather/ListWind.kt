package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class ListWind(
    @Json(name = "speed")
    val speed: Float?,
    @Json(name = "deg")
    val deg: Int?,
    @Json(name = "gust")
    val gust: Float?
)