package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.GroupHikeListHelper
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.flow.Flow
import java.util.*

interface IGroupHikeRepository {
    suspend fun listGroupHikesForLoggedInUser(
        isConnectedPage: Boolean
    ): Flow<ServerResponseResult<List<GroupHikeListHelper>>>

    suspend fun createGroupHikeForLoggedInUser(
        groupHikeName: String,
        dateTime: Calendar,
        route: Route.GroupHikeRoute
    ): Flow<ServerResponseResult<Boolean>>

    suspend fun loadRoute(groupHikeName: String): ServerResponseResult<Route.GroupHikeRoute>

    suspend fun listParticipants(groupHikeName: String): ServerResponseResult<List<String>>

    suspend fun generalConnectForLoggedInUser(
        groupHikeName: String,
        isConnectedPage: Boolean,
        dateTime: DateTime
    ): Flow<ResponseResult<Boolean>>
}