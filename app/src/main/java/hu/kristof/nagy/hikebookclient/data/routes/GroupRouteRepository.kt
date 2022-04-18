package hu.kristof.nagy.hikebookclient.data.routes

import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.routes.EditedGroupRoute
import hu.kristof.nagy.hikebookclient.model.routes.GroupRoute
import javax.inject.Inject

/**
 * A Repository that helps to
 * load, delete, create, and edit
 * group routes.
 */
class GroupRouteRepository  @Inject constructor(
    private val service: Service
) {
    suspend fun loadGroupRoutes(groupName: String): Result<List<GroupRoute>> {
        return handleRequest {
            service.loadGroupRoutes(groupName)
        }
    }

    suspend fun loadGroupRoute(groupName: String, routeName: String): Result<GroupRoute> {
        return handleRequest {
            service.loadGroupRoute(groupName, routeName)
        }
    }

    suspend fun deleteGroupRoute(groupName: String, routeName: String): Result<Boolean> {
        return handleRequest {
            service.deleteGroupRoute(groupName, routeName)
        }
    }

    suspend fun createGroupRoute(
        groupName: String,
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Result<Boolean> {
        return handleRequest {
            service.createGroupRoute(groupName, routeName,
                GroupRoute(groupName, routeName, points, hikeDescription)
            )
        }
    }

    suspend fun editGroupRoute(editedGroupRoute: EditedGroupRoute): Result<Boolean> {
        return handleRequest {
            service.editGroupRoute(
                editedGroupRoute.newGroupRoute.groupName,
                editedGroupRoute.oldGroupRoute.routeName,
                editedGroupRoute)
        }
    }
}