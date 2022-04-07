package hu.kristof.nagy.hikebookclient.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.EditedRoute
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRouteRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
    ) : IUserRouteRepository {
    override suspend fun loadUserRoutes(): Flow<Result<List<Route>>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            handleRequest {
                service.loadRoutes(userName!!, RouteType.USER)
            }
        }
    }

    override suspend fun deleteUserRoute(routeName: String): Flow<Result<Boolean>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            handleRequest {
                service.deleteRoute(userName!!, routeName, RouteType.USER)
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
            val userRoute = Route(userName!!, RouteType.USER, routeName, points, hikeDescription)
            handleRequest {
                service.createRoute(userName, userRoute.routeName, userRoute)
            }
        }
    }

    override suspend fun editUserRoute(editedUserRoute: EditedRoute): Result<Boolean> {
        return handleRequest {
            service.editRoute(
                editedUserRoute.newRoute.ownerName,
                editedUserRoute.oldRoute.routeName,
                editedUserRoute
            )
        }
    }
}