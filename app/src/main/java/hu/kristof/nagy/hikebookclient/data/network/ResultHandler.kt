package hu.kristof.nagy.hikebookclient.data.network

import android.content.Context
import android.widget.Toast
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult

/**
 * Handles the given result: if it's successful, then it executes the given lambda f.
 * If it's not successful, then it shows the error message.
 */
fun <T: Any> handleResult(context: Context?, res: ServerResponseResult<T>, f: (T) -> Unit) {
    if (res.isSuccess)
        f.invoke(res.successResult!!)
    else
        Toast.makeText(context, res.failMessage!!, Toast.LENGTH_LONG).show()
}

/**
 * Handles the given result: if it's successful, then it executes the given lambda f.
 * If it's not successful, then it shows the error message.
 */
fun <T: Any> handleResult(context: Context?, res: ResponseResult<T>, f: (T) -> Unit) {
    when(res) {
        is ResponseResult.Success<T> -> f.invoke(res.data)
        is ResponseResult.Error -> Toast.makeText(context, res.msg, Toast.LENGTH_LONG).show()
    }
}