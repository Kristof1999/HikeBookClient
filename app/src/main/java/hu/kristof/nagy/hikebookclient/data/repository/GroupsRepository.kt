package hu.kristof.nagy.hikebookclient.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A Repository that helps to create groups,
 * list and join/leave them.
 */
class GroupsRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
) : IGroupsRepository {
    override suspend fun listGroupsForLoggedInUser(
        isConnectedPage: Boolean
    ): Flow<ServerResponseResult<List<String>>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.listGroups(userName!!, isConnectedPage)
        }
    }

    override suspend fun createGroupForLoggedInUser(groupName: String): Flow<ServerResponseResult<Boolean>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.createGroup(groupName, userName!!)
        }
    }

    override suspend fun generalConnectForLoggedInUser(
        groupName: String,
        isConnectedPage: Boolean
    ): Flow<ResponseResult<Boolean>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            ResponseResult.from(
                service.generalGroupConnect(groupName, userName!!, isConnectedPage)
            )
        }
    }

    override suspend fun listMembers(groupName: String): ServerResponseResult<List<String>> {
        return service.listMembers(groupName)
    }
}