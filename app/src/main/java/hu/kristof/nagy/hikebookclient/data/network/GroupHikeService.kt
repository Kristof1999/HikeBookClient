package hu.kristof.nagy.hikebookclient.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface GroupHikeService {
    @GET("groupHike/{userName}/{isConnectedPage}")
    suspend fun listGroupHikes(
        @Path("userName") userName: String,
        @Path("isConnectedPage") isConnectedPage: Boolean
    ): List<String>
}