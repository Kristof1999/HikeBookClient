package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import retrofit2.http.*

interface RouteService {
    /**
     * Persists the created route if it's unique for the given user.
     * @param userName name of user who created the route
     * @param routeName name of the created route
     * @param points points of the created route
     * @return true if the route is unique
     */
    @PUT("routes/{userName}/{routeName}")
    suspend fun createRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String,
        @Body route: Route
    ): Boolean

    /**
     * Loads all the routes which belong to the given user.
     * @param userName the user's name for who to load the routes
     * @return list of routes which belong to the given user
     */
    @GET("routes/{userName}")
    suspend fun loadRoutesForUser(
        @Path("userName") userName: String
    ): List<Route>

    /**
     * Loads the points of the given route and user.
     * @param userName name of the user who requested the load
     * @param routeName name of the route for which to load the points
     * @return list of points of the route
     */
    @GET("routes/{userName}/{routeName}")
    suspend fun loadPoints(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String
    ): List<Point>

    /**
     * Lists all the routes' name and associated user name.
     */
    @GET("routes")
    suspend fun listRoutes(): List<BrowseListItem>

    /**
     * @param userName name of user who requested deletion
     * @param routeName name of route which to delete for the given user
     * @return true if deletion was successful
     */
    @DELETE("routes/{userName}/{routeName}")
    suspend fun deleteRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String
    ): Boolean

    /**
     * Edits the route.
     * @param userName name of user who requested editing
     * @param oldRouteName name of route before editing
     * @param route the edited route
     * @return true if the edited route is unique for the given user
     */
    @PUT("routes/edit/{userName}/{routeName}")
    suspend fun editRoute(
        @Path("userName") userName: String,
        @Path("routeName") oldRouteName: String,
        @Body route: Route
    ): Boolean
}