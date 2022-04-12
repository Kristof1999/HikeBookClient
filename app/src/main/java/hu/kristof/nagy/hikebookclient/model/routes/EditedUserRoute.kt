package hu.kristof.nagy.hikebookclient.model.routes

data class EditedUserRoute(
    val newUserRoute: UserRoute,
    val oldUserRoute: UserRoute
) : EditedRoute(newUserRoute, oldUserRoute)