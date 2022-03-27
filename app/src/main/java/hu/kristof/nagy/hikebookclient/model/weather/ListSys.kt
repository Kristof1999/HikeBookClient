package hu.kristof.nagy.hikebookclient.model.weather

import com.squareup.moshi.Json

data class ListSys(
    @Json(name = "pod")
    val p: String?
)