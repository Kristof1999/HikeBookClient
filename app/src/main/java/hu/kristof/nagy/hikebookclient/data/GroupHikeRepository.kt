package hu.kristof.nagy.hikebookclient.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.GroupHikeCreateHelper
import hu.kristof.nagy.hikebookclient.model.GroupHikeListHelper
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class GroupHikeRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
) {
    suspend fun listGroupHikes(
        isConnectedPage: Boolean
    ): Flow<List<GroupHikeListHelper>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.listGroupHikes(userName!!, isConnectedPage)
        }
    }

    suspend fun createGroupHike(
        groupHikeName: String,
        dateTime: Calendar,
        route: Route
    ): Flow<Result<Boolean>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            handleRequest {
                val dateTime = DateTime(dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH),
                    dateTime.get(Calendar.DAY_OF_MONTH), dateTime.get(Calendar.HOUR_OF_DAY),
                    dateTime.get(Calendar.MINUTE))
                val helper = GroupHikeCreateHelper(dateTime, route)
                service.createGroupHike(userName!!, groupHikeName, helper)
            }
        }
    }

    suspend fun loadRoute(groupHikeName: String): Route {
        return service.loadGroupHikeRoute(groupHikeName)
    }

    suspend fun listParticipants(groupHikeName: String): List<String> {
        return service.listParticipants(groupHikeName)
    }

    suspend fun generalConnect(
        groupHikeName: String,
        isConnectedPage: Boolean,
        dateTime: DateTime
    ): Flow<Boolean> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.generalGroupHikeConnect(groupHikeName, userName!!, isConnectedPage, dateTime)
        }
    }


}