package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.EditedRoute
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.model.RouteType
import retrofit2.http.*

interface RouteService {
    @PUT("routes/{ownerName}/{routeName}")
    suspend fun createRoute(
        @Path("ownerName") ownerName: String,
        @Path("routeName") routeName: String,
        @Body route: Route
    ): Boolean

    @GET("routes/{ownerName}/{routeType}")
    suspend fun loadRoutes(
        @Path("ownerName") ownerName: String,
        @Path("routeType") routeType: RouteType
    ): List<Route>

    @GET("routes/{ownerName}/{routeName}/{routeType}")
    suspend fun loadRoute(
        @Path("ownerName") ownerName: String,
        @Path("routeName") routeName: String,
        @Path("routeType") routeType: RouteType
    ): Route

    /**
     * Lists all the routes' name and associated user name.
     */
    @GET("routes/")
    suspend fun listUserRoutes(): List<BrowseListItem>

    @DELETE("routes/{ownerName}/{routeName}/{routeType}")
    suspend fun deleteRoute(
        @Path("ownerName") ownerName: String,
        @Path("routeName") routeName: String,
        @Path("routeType") routeType: RouteType
    ): Boolean

    @PUT("routes/edit/{ownerName}/{routeName}")
    suspend fun editRoute(
        @Path("ownerName") ownerName: String,
        @Path("routeName") oldRouteName: String,
        @Body route: EditedRoute
    ): Boolean
}