package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.Toast
import hu.kristof.nagy.hikebookclient.model.routes.Route

fun <P : Route> checkAndHandleRouteLoad(routeRes: Result<P>): Boolean {
    if (routeRes.isFailure) {
        throw IllegalStateException("Valami hiba történt.")
    } else {
        return checkAndHandleRouteLoad(routeRes.getOrNull())
    }
}

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