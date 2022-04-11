package hu.kristof.nagy.hikebookclient.model.routes

import hu.kristof.nagy.hikebookclient.model.Point

data class UserRoute(
    val userName: String,
    override val routeName: String,
    override val points: List<Point>,
    override val description: String
) : Route(routeName, points, description)