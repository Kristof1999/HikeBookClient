package hu.kristof.nagy.hikebookclient.data.routes

import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.EditedUserRoute
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import kotlinx.coroutines.flow.Flow

/**
 * A Repository that helps with
 * loading, listing, creating, editing, and deleting
 * user routes.
 */
interface IUserRouteRepository {
    suspend fun loadUserRoutesOfLoggedInUser(): Flow<ResponseResult<List<UserRoute>>>
    suspend fun loadUserRouteOfLoggedInUser(routeName: String): Flow<ResponseResult<UserRoute>>
    suspend fun listUserRoutesForLoggedInUser(): Flow<ResponseResult<List<BrowseListItem>>>
    suspend fun deleteUserRouteOfLoggedInUser(routeName: String): Flow<ResponseResult<Boolean>>
    suspend fun createUserRouteForLoggedInUser(
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<ResponseResult<Boolean>>
    suspend fun editUserRoute(editedUserRoute: EditedUserRoute): ResponseResult<Boolean>
}