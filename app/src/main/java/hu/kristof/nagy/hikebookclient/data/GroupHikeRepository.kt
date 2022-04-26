package hu.kristof.nagy.hikebookclient.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.*
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

/**
 * A Repository that helps to list group hikes,
 * create and join/leave them, and helps with
 * loading the route associated with the given group hike,
 * and helps to list the participants of a group hike.
 */
class GroupHikeRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
) {
    suspend fun listGroupHikesForLoggedInUser(
        isConnectedPage: Boolean
    ): Flow<ServerResponseResult<List<GroupHikeListHelper>>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            service.listGroupHikes(userName!!, isConnectedPage)
        }
    }

    suspend fun createGroupHikeForLoggedInUser(
        groupHikeName: String,
        dateTime: Calendar,
        route: Route
    ): Flow<ServerResponseResult<Boolean>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            val dateTime = DateTime(dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH), dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.MINUTE))
            val helper = GroupHikeCreateHelper(dateTime, route)
            service.createGroupHike(userName!!, groupHikeName, helper)
        }
    }

    suspend fun loadRoute(groupHikeName: String): ServerResponseResult<Route> {
        return service.loadGroupHikeRoute(groupHikeName)
    }

    suspend fun listParticipants(groupHikeName: String): ServerResponseResult<List<String>> {
        return service.listParticipants(groupHikeName)
    }

    suspend fun generalConnectForLoggedInUser(
        groupHikeName: String,
        isConnectedPage: Boolean,
        dateTime: DateTime
    ): Flow<ResponseResult<Boolean>> {
        return dataStore.data.map {
            it[Constants.DATA_STORE_USER_NAME]
        }.map { userName ->
            ResponseResult.from(
                service.generalGroupHikeConnect(groupHikeName, userName!!, isConnectedPage, dateTime)
            )
        }
    }
}