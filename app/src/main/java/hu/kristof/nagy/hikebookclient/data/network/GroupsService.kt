package hu.kristof.nagy.hikebookclient.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupsService {
    @GET("groups/{userName}/{isConnectedPage}")
    suspend fun listGroups(
        @Path("userName") userName: String,
        @Path("isConnectedPage") isConnectedPage: Boolean
    ): List<String>

    @PUT("groups/{groupName}")
    suspend fun createGroup(
        @Path("groupName") groupName: String,
        @Body userName: String
    ): Boolean
}