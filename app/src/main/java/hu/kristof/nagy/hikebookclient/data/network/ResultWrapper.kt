package hu.kristof.nagy.hikebookclient.data.network

import retrofit2.HttpException

suspend fun <T: Any> handleRequest(request: suspend () -> T): Result<T> {
    return try {
        Result.success(request.invoke())
    } catch (e: HttpException) {
        // TODO: decide if exception is worth rethrowing
        Result.failure(IllegalArgumentException(e.response()?.errorBody()?.string()))
    }
}