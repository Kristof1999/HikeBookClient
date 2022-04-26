package hu.kristof.nagy.hikebookclient.model.routes

/**
 * An EditedRoute representing a pair of GroupRoutes before and after editing.
 */
data class EditedGroupRoute(
    val newGroupRoute: Route.GroupRoute,
    val oldGroupRoute: Route.GroupRoute
) : EditedRoute(newGroupRoute, oldGroupRoute)
