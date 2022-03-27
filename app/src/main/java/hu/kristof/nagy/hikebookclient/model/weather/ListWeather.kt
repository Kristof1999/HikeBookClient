package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class ListWeather(
    @Json(name = "id")
    val id: Long?,
    @Json(name = "main")
    val main: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "icon")
    val icon: String?
)