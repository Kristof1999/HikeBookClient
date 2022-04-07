package hu.kristof.nagy.hikebookclient.model

data class GroupRoute(
    val groupName: String,
    override var routeName: String,
    override var points: List<Point>,
    override var description: String
) : Route(routeName, points, description)