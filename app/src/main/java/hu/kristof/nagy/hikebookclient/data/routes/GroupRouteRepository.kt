package hu.kristof.nagy.hikebookclient.data.routes

import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.routes.EditedGroupRoute
import hu.kristof.nagy.hikebookclient.model.routes.GroupRoute
import hu.kristof.nagy.hikebookclient.model.routes.Route
import javax.inject.Inject

class GroupRouteRepository  @Inject constructor(
    private val service: Service
) {
    suspend fun loadRoutes(groupName: String): Result<List<Route>> {
        return handleRequest {
            service.loadGroupRoutes(groupName)
        }
    }

    suspend fun deleteRoute(groupName: String, routeName: String): Result<Boolean> {
        return handleRequest {
            service.deleteGroupRoute(groupName, routeName)
        }
    }

    suspend fun createRoute(
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

    suspend fun editGroupRoute(groupName: String, editedGroupRoute: EditedGroupRoute): Result<Boolean> {
        return handleRequest {
            service.editGroupRoute(
                groupName,
                editedGroupRoute.oldGroupRoute.routeName,
                editedGroupRoute)
        }
    }
}