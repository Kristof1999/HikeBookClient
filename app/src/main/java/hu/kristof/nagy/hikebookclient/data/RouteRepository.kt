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
    ) : IRouteRepository {
    override suspend fun loadRoutesForLoggedInUser(): Flow<List<Route>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.loadRoutesForUser(userName!!)
        }
    }

    override suspend fun deleteRoute(routeName: String): Flow<Boolean> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.deleteRoute(userName!!, routeName)
        }
    }

    override suspend fun createRoute(route: Route): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            val route = Route(route.routeName, route.points, route.description)
            service.createRoute(userName!!, route.routeName, route)
        }
    }

    override suspend fun editRoute(oldRouteName: String, route: Route): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.editRoute(userName!!, oldRouteName, route)
        }
    }
}