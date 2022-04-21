package hu.kristof.nagy.hikebookclient.data.network.routes

import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.EditedGroupRoute
import hu.kristof.nagy.hikebookclient.model.routes.GroupRoute
import retrofit2.http.*

interface GroupRouteService {
    @PUT("groups/routes/{groupName}/{routeName}")
    suspend fun createGroupRoute(
        @Path("groupName") groupName: String,
        @Path("routeName") routeName: String,
        @Body groupRoute: GroupRoute
    ): ResponseResult<Boolean>

    @GET("groups/routes/{groupName}")
    suspend fun loadGroupRoutes(
        @Path("groupName") groupName: String
    ): List<GroupRoute>

    @GET("groups/routes/{groupName}/{routeName}")
    suspend fun loadGroupRoute(
        @Path("groupName") groupName: String,
        @Path("routeName") routeName: String
    ): GroupRoute

    @DELETE("groups/routes/{groupName}/{routeName}")
    suspend fun deleteGroupRoute(
        @Path("groupName") groupName: String,
        @Path("routeName") routeName: String
    ): Boolean

    @PUT("groups/routes/edit/{groupName}/{routeName}")
    suspend fun editGroupRoute(
        @Path("groupName") groupName: String,
        @Path("routeName") oldRouteName: String,
        @Body editedGroupRoute: EditedGroupRoute
    ): Boolean
}