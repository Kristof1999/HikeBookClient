package hu.kristof.nagy.hikebookclient.data.network

import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupsService {
    @GET("groups/{userName}/{isConnectedPage}")
    suspend fun listGroups(
        @Path("userName") userName: String,
        @Path("isConnectedPage") isConnectedPage: Boolean
    ): List<String>

    @PUT("groups/{groupName}/{userName}")
    suspend fun createGroup(
        @Path("groupName") groupName: String,
        @Path("userName") userName: String
    ): Boolean
}