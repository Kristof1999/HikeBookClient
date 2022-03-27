package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class ListResponse(
    @Json(name = "dt")
    val dt: Long?,
    @Json(name = "main")
    val main: ListMain?,
    @Json(name = "weather")
    val weather: List<ListWeather>?,
    @Json(name = "clouds")
    val clouds: ListClouds?,
    @Json(name = "wind")
    val wind: ListWind?,
    @Json(name = "visibility")
    val visibility: Int?,
    @Json(name = "pop")
    val pop: Float?,
    @Json(name = "rain")
    val rain: ListRain?,
    @Json(name = "snow")
    val snow: ListSnow?,
    @Json(name = "sys")
    val sys: ListSys?,
    @Json(name = "dt_txt")
    val dtTxt: String?
)
