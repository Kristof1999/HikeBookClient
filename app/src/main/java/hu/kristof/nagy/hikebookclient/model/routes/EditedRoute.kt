package hu.kristof.nagy.hikebookclient.model.routes

/**
 * A Class representing a pair of routes: before and after editing.
 */
sealed class EditedRoute(val newRoute: Route, val oldRoute: Route) {
    /**
     * An EditedRoute representing a pair of UserRoutes before and after editing.
     */
    data class EditedUserRoute(
        val newUserRoute: Route.UserRoute,
        val oldUserRoute: Route.UserRoute
    ) : EditedRoute(newUserRoute, oldUserRoute)

    /**
     * An EditedRoute representing a pair of GroupRoutes before and after editing.
     */
    data class EditedGroupRoute(
        val newGroupRoute: Route.GroupRoute,
        val oldGroupRoute: Route.GroupRoute
    ) : EditedRoute(newGroupRoute, oldGroupRoute)
}