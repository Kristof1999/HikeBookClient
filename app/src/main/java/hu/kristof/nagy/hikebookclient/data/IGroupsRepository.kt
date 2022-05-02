package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import kotlinx.coroutines.flow.Flow

interface IGroupsRepository {
    suspend fun listGroupsForLoggedInUser(
        isConnectedPage: Boolean
    ): Flow<ServerResponseResult<List<String>>>
    suspend fun createGroupForLoggedInUser(groupName: String): Flow<ServerResponseResult<Boolean>>
    suspend fun generalConnectForLoggedInUser(
        groupName: String,
        isConnectedPage: Boolean
    ): Flow<ResponseResult<Boolean>>
    suspend fun listMembers(groupName: String): ServerResponseResult<List<String>>
}