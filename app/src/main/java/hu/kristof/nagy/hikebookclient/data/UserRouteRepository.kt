package hu.kristof.nagy.hikebookclient.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.model.UserRoute
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRouteRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
    ) : IUserRouteRepository {
    override suspend fun loadUserRoutes(): Flow<List<UserRoute>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.loadUserRoutesForUser(userName!!)
        }
    }

    override suspend fun deleteUserRoute(routeName: String): Flow<Boolean> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.deleteUserRoute(userName!!, routeName)
        }
    }

    override suspend fun createUserRoute(
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            val userRoute = UserRoute(userName!!, routeName, points, hikeDescription)
            service.createUserRouteForUser(userName!!, userRoute.routeName, userRoute)
        }
    }

    override suspend fun editUserRoute(
        oldRouteName: String,
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            val userRoute = UserRoute(userName!!, routeName, points, hikeDescription)
            service.editUserRoute(userName!!, oldRouteName, userRoute)
        }
    }
}