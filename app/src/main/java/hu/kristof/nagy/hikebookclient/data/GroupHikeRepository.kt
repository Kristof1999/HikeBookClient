package hu.kristof.nagy.hikebookclient.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupHikeRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
) {
    suspend fun listGroupHikes(
        isConnectedPage: Boolean
    ): Flow<List<String>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.listGroupHikes(userName!!, isConnectedPage)
        }
    }
}