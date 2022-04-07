package hu.kristof.nagy.hikebookclient.model

class EditedUserRoute(
    val newUserRoute: UserRoute,
    val oldUserRoute: UserRoute
) : EditedRoute(newUserRoute, oldUserRoute)