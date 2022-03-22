package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.model.UserRoute
import kotlinx.coroutines.flow.Flow

interface IUserRouteRepository {
    suspend fun loadUserRoutes(): Flow<List<UserRoute>>
    suspend fun deleteUserRoute(routeName: String): Flow<Boolean>
    suspend fun createUserRoute(
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<Boolean>
    suspend fun editUserRoute(
        oldRouteName: String,
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<Boolean>
}