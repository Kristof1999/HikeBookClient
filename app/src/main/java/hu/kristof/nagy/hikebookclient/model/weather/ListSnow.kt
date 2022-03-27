package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class ListSnow(
    @Json(name = "3h")
    val vol: Int?
)