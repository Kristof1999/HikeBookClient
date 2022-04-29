package hu.kristof.nagy.hikebookclient.data.routes

import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.EditedRoute
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.flow.Flow

/**
 * A Repository that helps with
 * loading, listing, creating, editing, and deleting
 * user routes.
 */
interface IUserRouteRepository {
    suspend fun loadUserRoutesOfLoggedInUser(): Flow<ServerResponseResult<List<Route.UserRoute>>>
    suspend fun loadUserRouteOfLoggedInUser(routeName: String): Flow<ServerResponseResult<Route.UserRoute>>
    suspend fun listUserRoutesForLoggedInUser(): Flow<ServerResponseResult<List<BrowseListItem>>>
    suspend fun loadUserRouteOfUser(
        userName: String,
        routeName: String
    ): ServerResponseResult<Route.UserRoute>
    suspend fun deleteUserRouteOfLoggedInUser(routeName: String): Flow<ResponseResult<Boolean>>
    suspend fun createUserRouteForLoggedInUser(
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<ResponseResult<Boolean>>
    suspend fun editUserRoute(editedUserRoute: EditedRoute.EditedUserRoute): ResponseResult<Boolean>
}