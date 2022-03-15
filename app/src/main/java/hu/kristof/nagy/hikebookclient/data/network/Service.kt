package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.model.UserAuth
import hu.kristof.nagy.hikebookclient.model.UserRoute
import retrofit2.http.*

interface Service {
    /**
     * Logs in the given user if there exists one in the database
     * with the given name and password.
     * @return true if login was successful
     */
    @POST("login")
    suspend fun login(@Body user: UserAuth): Boolean

    /**
     * Registers the user if there is no other user
     * in the database with the same name.
     * @return true if registration was successful
     */
    @POST("register")
    suspend fun register(@Body user: UserAuth): Boolean

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

    @GET("routes")
    suspend fun loadRoutes(): List<UserRoute>

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
