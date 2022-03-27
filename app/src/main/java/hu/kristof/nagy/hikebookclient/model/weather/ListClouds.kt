package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class ListClouds(
    @Json(name = "all")
    val all: Int?
)