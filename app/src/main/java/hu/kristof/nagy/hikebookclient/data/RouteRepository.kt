package hu.kristof.nagy.hikebookclient.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RouteRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
    ) {
    suspend fun loadRoutesForLoggedInUser(): Flow<List<Route>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.loadRoutesForUser(userName!!)
        }
    }

    suspend fun deleteRoute(routeName: String): Flow<Boolean> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.deleteRoute(userName!!, routeName)
        }
    }

    suspend fun createRoute(route: Route): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.createRoute(userName!!, route.routeName, route.points)
        }
    }

    suspend fun editRoute(oldRouteName: String, route: Route): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.editRoute(userName!!, oldRouteName, route)
        }
    }
}