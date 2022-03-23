package hu.kristof.nagy.hikebookclient.data.network

import android.content.Context
import android.widget.Toast
import retrofit2.HttpException

suspend fun <T: Any> handleRequest(request: suspend () -> T): Result<T> {
    return try {
        Result.success(request.invoke())
    } catch (e: HttpException) {
        // TODO: decide if exception is worth rethrowing
        Result.failure(IllegalArgumentException(e.response()?.errorBody()?.string()))
    }
}

fun <T: Any> handleResult(context: Context?, res:Result<T>, f: (T) -> Unit) {
    if (res.isSuccess)
        f.invoke(res.getOrNull()!!)
    else
        Toast.makeText(context, res.exceptionOrNull()!!.message, Toast.LENGTH_LONG).show()
}