package hu.kristof.nagy.hikebookclient.data.routes

import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import kotlinx.coroutines.flow.Flow

interface IUserRouteRepository {
    suspend fun loadUserRoutes(): Flow<Result<List<Route>>>
    suspend fun deleteUserRoute(routeName: String): Flow<Result<Boolean>>
    suspend fun createUserRoute(
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<Result<Boolean>>
}