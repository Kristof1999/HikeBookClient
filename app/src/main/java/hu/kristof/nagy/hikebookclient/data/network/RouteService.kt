package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import retrofit2.http.*

interface RouteService {
    @PUT("routes/{userName}/{routeName}")
    suspend fun createRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String,
        @Body points: List<Point>
    ): Boolean

    @GET("routes/{userName}")
    suspend fun loadRoutesForUser(
        @Path("userName") userName: String
    ): List<Route>

    @GET("routes/{userName}/{routeName}")
    suspend fun loadPoints(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String
    ): List<Point>

    @GET("routes")
    suspend fun listRoutes(): List<BrowseListItem>

    @DELETE("routes/{userName}/{routeName}")
    suspend fun deleteRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String
    ): Boolean

    @PUT("routes/edit/{userName}/{routeName}")
    suspend fun editRoute(
        @Path("userName") userName: String,
        @Path("routeName") oldRouteName: String,
        @Body route: Route
    ): Boolean
}