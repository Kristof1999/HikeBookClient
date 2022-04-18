package hu.kristof.nagy.hikebookclient.model.routes

/**
 * An EditedRoute representing a pair of UserRoutes before and after editing.
 */
data class EditedUserRoute(
    val newUserRoute: UserRoute,
    val oldUserRoute: UserRoute
) : EditedRoute(newUserRoute, oldUserRoute)