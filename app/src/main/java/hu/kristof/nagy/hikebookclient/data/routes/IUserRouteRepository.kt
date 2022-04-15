package hu.kristof.nagy.hikebookclient.data.routes

import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.routes.EditedUserRoute
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import kotlinx.coroutines.flow.Flow

interface IUserRouteRepository {
    suspend fun loadUserRoutes(): Flow<Result<List<UserRoute>>>
    suspend fun loadUserRoute(routeName: String): Flow<Result<UserRoute>>
    suspend fun deleteUserRoute(routeName: String): Flow<Result<Boolean>>
    suspend fun createUserRoute(
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<Result<Boolean>>
    suspend fun editUserRoute(editedUserRoute: EditedUserRoute): Result<Boolean>
}