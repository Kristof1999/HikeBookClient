package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class ListMain(
    @Json(name = "temp")
    val temp: Float?,
    @Json(name = "feels_like")
    val feelsLike: Float?,
    @Json(name = "temp_min")
    val tempMin: Float?,
    @Json(name = "temp_max")
    val tempMax: Float?,
    @Json(name = "pressure")
    val pressure: Int?,
    @Json(name = "sea_level")
    val seaLevel: Int?,
    @Json(name = "grnd_level")
    val grndLevel: Int?,
    @Json(name = "humidity")
    val humidity: Int?,
    @Json(name = "temp_kf")
    val tempKf: Float?
)
