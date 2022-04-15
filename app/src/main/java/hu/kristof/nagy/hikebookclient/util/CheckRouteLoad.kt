package hu.kristof.nagy.hikebookclient.util

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