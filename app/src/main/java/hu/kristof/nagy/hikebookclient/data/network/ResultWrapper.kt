package hu.kristof.nagy.hikebookclient.data.network

import android.content.Context
import android.widget.Toast
import retrofit2.HttpException

/**
 * Handles the HTTP request: catches Internal Server Errors
 * and gives it back as an IllegalArgumentException encapsulated in a Result.
 * If no Internal Server Errors are thrown, then it gives back an
 * Exception encapsulated in a Result.
 * If the request was successful, it gives back the response
 * encapsulated in a Result.
 */
suspend fun <T: Any> handleRequest(request: suspend () -> T): Result<T> {
    return try {
        Result.success(request.invoke())
    } catch (e: HttpException) {
        if (e.response()?.code() == 500) {
            Result.failure(IllegalArgumentException(e.response()?.errorBody()?.string()))
        } else {
            Result.failure(Exception(e.response()?.errorBody()?.string()))
        }
    }
}

/**
 * Handles the given result: if it's successful, then it executes the given lambda f.
 * If it's not successful and the exception is an IllegalArgumentException,
 * then it shows the error message.
 */
fun <T: Any> handleResult(context: Context?, res: Result<T>, f: (T) -> Unit) {
    if (res.isSuccess)
        f.invoke(res.getOrNull()!!)
    else if(res.exceptionOrNull()!! is IllegalArgumentException)
        Toast.makeText(context, res.exceptionOrNull()!!.message, Toast.LENGTH_LONG).show()
}