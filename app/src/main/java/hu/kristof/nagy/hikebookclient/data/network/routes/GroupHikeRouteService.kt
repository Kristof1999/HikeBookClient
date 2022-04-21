package hu.kristof.nagy.hikebookclient.data.network.routes

import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import retrofit2.http.GET
import retrofit2.http.Path

interface GroupHikeRouteService {
    @GET("groupHike/routes/{groupHikeName}")
    suspend fun loadGroupHikeRoute(
        @Path("groupHikeName") groupHikeName: String
    ): ResponseResult<Route>
}