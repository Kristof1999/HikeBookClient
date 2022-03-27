package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class CityCoord(
    @Json(name = "lat")
    val lat: Float?,
    @Json(name = "lon")
    val lon: Float?
)