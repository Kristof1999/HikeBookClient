package hu.kristof.nagy.hikebookclient.data.routes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.routes.EditedUserRoute
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRouteRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
    ) : IUserRouteRepository {
    override suspend fun loadUserRoutes(): Flow<Result<List<UserRoute>>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            handleRequest {
                service.loadUserRoutes(userName!!)
            }
        }
    }

    override suspend fun loadUserRoute(routeName: String): Flow<Result<UserRoute>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            handleRequest {
                service.loadUserRoute(userName!!, routeName)
            }
        }
    }

    override suspend fun deleteUserRoute(routeName: String): Flow<Result<Boolean>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            handleRequest {
                service.deleteUserRoute(userName!!, routeName)
            }
        }
    }

    override suspend fun createUserRoute(
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<Result<Boolean>> {
        return dataStore.data.map { preferences ->
            preferences[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            val userRoute = UserRoute(userName!!, routeName, points, hikeDescription)
            handleRequest {
                service.createUserRoute(userName, userRoute.routeName, userRoute)
            }
        }
    }

    override suspend fun editUserRoute(editedUserRoute: EditedUserRoute): Result<Boolean> {
        return handleRequest {
            service.editUserRoute(
                editedUserRoute.newUserRoute.userName,
                editedUserRoute.oldUserRoute.routeName,
                editedUserRoute
            )
        }
    }
}