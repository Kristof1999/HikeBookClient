package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import hu.kristof.nagy.hikebookclient.model.routes.Route

fun <P : Route> routeLoaded(route: LiveData<Result<P>>): Boolean {
    if (route.value == null) {
        throw IllegalStateException("Az útvonal még nem töltődött be! Kérem, várjon.")
    } else if (route.value!!.isFailure) {
        throw IllegalStateException("Valami hiba történt.")
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