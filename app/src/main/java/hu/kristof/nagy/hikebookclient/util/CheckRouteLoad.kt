package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route

/**
 * Checks if the route has been loaded.
 * @throws IllegalStateException if the route has not been loaded
 * @return true if the route has loaded
 */
fun <P : Route> checkAndHandleRouteLoad(routeRes: ServerResponseResult<P>): Boolean {
    if (!routeRes.isSuccess) {
        throw IllegalStateException("Valami hiba történt.")
    } else {
        return checkAndHandleRouteLoad(routeRes.successResult)
    }
}

/**
 * Checks if the route has been loaded.
 * @throws IllegalStateException if the route has not been loaded
 * @return true if the route has loaded
 */
fun <P : Route?> checkAndHandleRouteLoad(route: P): Boolean {
    if (route == null) {
        throw IllegalStateException("Az útvonal még nem töltődött be! Kérem, várjon.")
    } else {
        return true
    }
}

fun catchAndShowIllegalStateAndArgument(context: Context?, f: () -> Unit) {
    try {
        f.invoke()
    } catch (e: IllegalStateException) {
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    } catch (e: IllegalArgumentException) {
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    }
}

suspend fun <T> handleIllegalStateAndArgument(
    data: MutableLiveData<ResponseResult<T>>,
    f: suspend () -> Unit
) {
    try {
        f.invoke()
    } catch (e: IllegalStateException) {
        data.value = ResponseResult.Error(e.message!!)
    } catch (e: IllegalArgumentException) {
        data.value = ResponseResult.Error(e.message!!)
    }
}