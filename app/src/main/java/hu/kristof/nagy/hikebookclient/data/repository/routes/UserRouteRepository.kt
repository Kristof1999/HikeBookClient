package hu.kristof.nagy.hikebookclient.data.repository.routes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.EditedRoute
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRouteRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
    ) : IUserRouteRepository {
    override suspend fun loadUserRoutesOfLoggedInUser(): Flow<ServerResponseResult<List<Route.UserRoute>>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.loadUserRoutes(userName!!)
        }
    }

    override suspend fun loadUserRouteOfLoggedInUser(
        routeName: String
    ): Flow<ServerResponseResult<Route.UserRoute>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.loadUserRoute(userName!!, routeName)
        }
    }

    override suspend fun listUserRoutesForLoggedInUser()
    : Flow<ServerResponseResult<List<BrowseListItem>>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.listUserRoutes(userName!!)
        }
    }

    override suspend fun loadUserRouteOfUser(
        userName: String,
        routeName: String
    ): ServerResponseResult<Route.UserRoute> {
        return wrapEspressoIdlingResource {
            service.loadUserRoute(userName, routeName)
        }
    }

    override suspend fun deleteUserRouteOfLoggedInUser(
        routeName: String
    ): Flow<ResponseResult<Boolean>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            ResponseResult.from(
                service.deleteUserRoute(userName!!, routeName)
            )
        }
    }

    override suspend fun createUserRouteForLoggedInUser(
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<ResponseResult<Boolean>> {
        return dataStore.data.map { preferences ->
            preferences[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            val userRoute = Route.UserRoute(userName!!, routeName, points, hikeDescription)
            ResponseResult.from(
                service.createUserRoute(userName, userRoute.routeName, userRoute)
            )
        }
    }

    override suspend fun editUserRoute(editedUserRoute: EditedRoute.EditedUserRoute): ResponseResult<Boolean> {
        return ResponseResult.from(
            service.editUserRoute(
                editedUserRoute.newUserRoute.userName,
                editedUserRoute.oldUserRoute.routeName,
                editedUserRoute
            )
        )
    }
}