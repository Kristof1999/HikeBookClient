package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.Route
import kotlinx.coroutines.flow.Flow

interface IRouteRepository {
    suspend fun loadRoutesForLoggedInUser(): Flow<List<Route>>
    suspend fun deleteRoute(routeName: String): Flow<Boolean>
    suspend fun createRoute(route: Route): Flow<Boolean>
    suspend fun editRoute(oldRouteName: String, route: Route): Flow<Boolean>
}