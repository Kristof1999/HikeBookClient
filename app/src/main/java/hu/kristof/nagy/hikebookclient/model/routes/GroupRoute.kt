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
        checkGroupName(groupName)
        checkRouteName(routeName)
        checkPointSize(points)
    }

    companion object {
        fun checkGroupName(groupName: String) {
            if (groupName.isEmpty())
                throw IllegalArgumentException("A név nem lehet üres.")
            if (groupName.contains("/"))
                throw IllegalArgumentException("A név nem tartalmazhat / jelet.")
        }
    }
}