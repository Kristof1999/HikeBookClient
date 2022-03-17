package hu.kristof.nagy.hikebookclient.util

import hu.kristof.nagy.hikebookclient.model.Route

object RouteUtils {
    /**
     * Checks if the given route is not empty, does not contains /,
     * and has at least 2 points.
     */
    fun checkRoute(route: Route) {
        if (route.routeName.isEmpty())
            throw IllegalArgumentException("Az útvonal név nem lehet üres.")
        if (route.routeName.contains("/"))
            throw IllegalArgumentException("Az útvonal név nem tartalmazhat / jelet.")
        if (route.points.size < 2)
            throw IllegalArgumentException("Az útvonalnak legalább 2 pontból kell állnia.")
    }
}