package hu.kristof.nagy.hikebookclient.model

import com.squareup.moshi.Json

data class ServerResponseResult<T>(
    @Json(name = "isSuccess")
    val isSuccess: Boolean,
    val failMessage: String?,
    val successResult: T?
)