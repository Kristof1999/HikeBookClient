package hu.kristof.nagy.hikebookclient.model

sealed class ResponseResult <T> {
    data class Success<T>(val data: T) : ResponseResult<T>()
    data class Error<T>(val msg: String) : ResponseResult<T>()
    companion object {
        fun <T> from(serverResponseResult: ServerResponseResult<T>): ResponseResult<T> {
            return if (serverResponseResult.isSuccess) {
                Success(serverResponseResult.successResult!!)
            } else {
                Error(serverResponseResult.failMessage!!)
            }
        }
    }
}