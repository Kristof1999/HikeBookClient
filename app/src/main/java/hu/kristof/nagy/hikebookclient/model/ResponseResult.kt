package hu.kristof.nagy.hikebookclient.model

data class ResponseResult<T>(
    val isSuccess: Boolean?,
    val failMessage: String?,
    val successResult: T?
)