package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.EditedGroupRoute
import hu.kristof.nagy.hikebookclient.model.GroupRoute
import hu.kristof.nagy.hikebookclient.model.Point
import javax.inject.Inject

class GroupRouteRepository  @Inject constructor(
    private val service: Service
) {
    suspend fun loadRoutes(groupName: String): Result<List<GroupRoute>> {
        return handleRequest {
            service.loadGroupRoutesForGroup(groupName)
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
            service.createGroupRouteForGroup(groupName, routeName,
                GroupRoute(groupName, routeName, points, hikeDescription)
            )
        }
    }

    suspend fun editRoute(groupName: String, editedRoute: EditedGroupRoute): Result<Boolean> {
        return handleRequest {
            service.editGroupRoute(
                groupName,
                editedRoute.oldGroupRoute.routeName,
                editedRoute)
        }
    }
}