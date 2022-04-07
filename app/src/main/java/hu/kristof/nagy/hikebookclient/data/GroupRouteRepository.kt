package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.EditedRoute
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.model.RouteType
import javax.inject.Inject

class GroupRouteRepository  @Inject constructor(
    private val service: Service
) {
    suspend fun loadRoutes(groupName: String): Result<List<Route>> {
        return handleRequest {
            service.loadRoutes(groupName, RouteType.GROUP)
        }
    }

    suspend fun deleteRoute(groupName: String, routeName: String): Result<Boolean> {
        return handleRequest {
            service.deleteRoute(groupName, routeName, RouteType.GROUP)
        }
    }

    suspend fun createRoute(
        groupName: String,
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Result<Boolean> {
        return handleRequest {
            service.createRoute(groupName, routeName,
                Route(groupName, RouteType.GROUP, routeName, points, hikeDescription)
            )
        }
    }

    suspend fun editRoute(groupName: String, editedRoute: EditedRoute): Result<Boolean> {
        return handleRequest {
            service.editRoute(
                groupName,
                editedRoute.oldRoute.routeName,
                editedRoute)
        }
    }
}