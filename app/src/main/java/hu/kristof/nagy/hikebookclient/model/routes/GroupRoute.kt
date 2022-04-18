package hu.kristof.nagy.hikebookclient.model.routes

import hu.kristof.nagy.hikebookclient.model.Point

/**
 * A Route representing a route that belongs to a group.
 */
data class GroupRoute(
    val groupName: String,
    override val routeName: String,
    override val points: List<Point>,
    override val description: String
) : Route(routeName, points, description) {
    init {
        checkRouteName(routeName)
        checkPointSize(points)
    }
}