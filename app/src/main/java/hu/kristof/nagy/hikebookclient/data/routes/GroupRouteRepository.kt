package hu.kristof.nagy.hikebookclient.data.routes

import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.EditedRoute
import hu.kristof.nagy.hikebookclient.model.routes.Route
import javax.inject.Inject

/**
 * A Repository that helps to
 * load, delete, create, and edit
 * group routes.
 */
class GroupRouteRepository  @Inject constructor(
    private val service: Service
) {
    suspend fun loadGroupRoutes(groupName: String): ServerResponseResult<List<Route.GroupRoute>> {
        return service.loadGroupRoutes(groupName)
    }

    suspend fun loadGroupRoute(groupName: String, routeName: String): ServerResponseResult<Route.GroupRoute> {
        return service.loadGroupRoute(groupName, routeName)
    }

    suspend fun deleteGroupRoute(groupName: String, routeName: String): ServerResponseResult<Boolean> {
        return service.deleteGroupRoute(groupName, routeName)
    }

    suspend fun createGroupRoute(
        groupName: String,
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): ResponseResult<Boolean> {
        return ResponseResult.from(
            service.createGroupRoute(groupName, routeName,
                Route.GroupRoute(groupName, routeName, points, hikeDescription)
            )
        )
    }

    suspend fun editGroupRoute(
        editedGroupRoute: EditedRoute.EditedGroupRoute
    ): ResponseResult<Boolean> {
        return ResponseResult.from(
            service.editGroupRoute(
                editedGroupRoute.newGroupRoute.groupName,
                editedGroupRoute.oldGroupRoute.routeName,
                editedGroupRoute
            )
        )
    }
}