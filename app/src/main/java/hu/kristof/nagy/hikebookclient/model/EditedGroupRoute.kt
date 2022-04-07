package hu.kristof.nagy.hikebookclient.model

class EditedGroupRoute(
    val newGroupRoute: GroupRoute,
    val oldGroupRoute: GroupRoute
) : EditedRoute(newGroupRoute, oldGroupRoute)