package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.EditedGroupRoute
import hu.kristof.nagy.hikebookclient.model.GroupRoute
import retrofit2.http.*

interface GroupRouteService {
    @PUT("routes/groups/{groupName}/{routeName}")
    suspend fun createGroupRouteForGroup(
        @Path("groupName") groupName: String,
        @Path("routeName") routeName: String,
        @Body route: GroupRoute
    ): Boolean

    @GET("routes/groups/{groupName}")
    suspend fun loadGroupRoutesForGroup(
        @Path("groupName") groupName: String
    ): List<GroupRoute>

    @GET("routes/groups/{groupName}/{routeName}")
    suspend fun loadGroupRoute(
        @Path("groupName") groupName: String,
        @Path("routeName") routeName: String
    ): GroupRoute

    @DELETE("routes/groups/{groupName}/{routeName}")
    suspend fun deleteGroupRoute(
        @Path("groupName") groupName: String,
        @Path("routeName") routeName: String
    ): Boolean

    @PUT("routes/groups/edit/{groupName}/{routeName}")
    suspend fun editGroupRoute(
        @Path("groupName") groupName: String,
        @Path("routeName") oldRouteName: String,
        @Body route: EditedGroupRoute
    ): Boolean
}