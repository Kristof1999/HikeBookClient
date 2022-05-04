package hu.kristof.nagy.hikebookclient.data.repository.routes

import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.EditedRoute
import hu.kristof.nagy.hikebookclient.model.routes.Route

interface IGroupRouteRepository {
    suspend fun loadGroupRoutes(
        groupName: String
    ): ServerResponseResult<List<Route.GroupRoute>>
    suspend fun loadGroupRoute(
        groupName: String,
        routeName: String
    ): ServerResponseResult<Route.GroupRoute>
    suspend fun deleteGroupRoute(
        groupName: String,
        routeName: String
    ): ServerResponseResult<Boolean>
    suspend fun createGroupRoute(
        groupName: String,
        routeName: String,
        points: List<Point>,
        hikeDescription: String
    ): ResponseResult<Boolean>
    suspend fun editGroupRoute(
        editedGroupRoute: EditedRoute.EditedGroupRoute
    ): ResponseResult<Boolean>
}