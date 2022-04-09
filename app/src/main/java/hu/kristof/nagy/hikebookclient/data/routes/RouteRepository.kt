package hu.kristof.nagy.hikebookclient.data.routes

import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.EditedRoute
import javax.inject.Inject

class RouteRepository @Inject constructor(
    private val service: Service
    ) {
    suspend fun editRoute(editedRoute: EditedRoute): Result<Boolean> {
        return handleRequest {
            service.editRoute(
                editedRoute.newRoute.ownerName,
                editedRoute.oldRoute.routeName,
                editedRoute
            )
        }
    }
}