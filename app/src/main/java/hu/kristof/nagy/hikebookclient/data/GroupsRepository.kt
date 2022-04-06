package hu.kristof.nagy.hikebookclient.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupsRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
) {
    suspend fun listGroups(
        isConnectedPage: Boolean
    ): Flow<List<String>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.listGroups(userName!!, isConnectedPage)
        }
    }

    suspend fun createGroup(groupName: String): Flow<Result<Boolean>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            handleRequest {
                service.createGroup(groupName, userName!!)
            }
        }
    }

    suspend fun generalConnect(groupName: String, isConnectedPage: Boolean): Flow<Boolean> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.generalConnect(groupName, userName!!, isConnectedPage)
        }
    }
}