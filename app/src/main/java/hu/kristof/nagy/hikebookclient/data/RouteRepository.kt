package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.EditedRoute
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    suspend fun loadRoutes(): Flow<Result<List<Route>>>
    suspend fun deleteRoute(routeName: String): Flow<Result<Boolean>>
    suspend fun createRoute(
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<Result<Boolean>>
    suspend fun editRoute(
        editedRoute: EditedRoute
    ): Result<Boolean>
}