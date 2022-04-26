package hu.kristof.nagy.hikebookclient.model.routes

/**
 * An EditedRoute representing a pair of UserRoutes before and after editing.
 */
data class EditedUserRoute(
    val newUserRoute: Route.UserRoute,
    val oldUserRoute: Route.UserRoute
) : EditedRoute(newUserRoute, oldUserRoute)