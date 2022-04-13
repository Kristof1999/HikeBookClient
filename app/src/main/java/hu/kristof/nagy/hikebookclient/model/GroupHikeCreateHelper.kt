package hu.kristof.nagy.hikebookclient.model

import hu.kristof.nagy.hikebookclient.model.routes.Route

data class GroupHikeCreateHelper(
    val year: Int, val month: Int, val dayOfMonth: Int,
    val hourOfDay: Int, val minute: Int,
    val route: Route
)
