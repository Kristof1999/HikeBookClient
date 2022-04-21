package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.GroupHikeCreateHelper
import hu.kristof.nagy.hikebookclient.model.GroupHikeListHelper
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupHikeService {
    @GET("groupHike/{userName}/{isConnectedPage}")
    suspend fun listGroupHikes(
        @Path("userName") userName: String,
        @Path("isConnectedPage") isConnectedPage: Boolean
    ): ResponseResult<List<GroupHikeListHelper>>

    @PUT("groupHike/{userName}/{groupHikeName}")
    suspend fun createGroupHike(
        @Path("userName") userName: String,
        @Path("groupHikeName") groupHikeName: String,
        @Body helper: GroupHikeCreateHelper
    ): ResponseResult<Boolean>

    @GET("groupHike/{groupHikeName}")
    suspend fun listParticipants(
        @Path("groupHikeName") groupHikeName: String
    ): ResponseResult<List<String>>

    @PUT("groupHike/{groupHikeName}/{userName}/{isConnectedPage}")
    suspend fun generalGroupHikeConnect(
        @Path("groupHikeName") groupHikeName: String,
        @Path("userName") userName: String,
        @Path("isConnectedPage") isConnectedPage: Boolean,
        @Body dateTime: DateTime
    ): ResponseResult<Boolean>
}