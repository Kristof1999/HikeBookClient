package hu.kristof.nagy.hikebookclient.data.network.routes

import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.routes.EditedUserRoute
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import retrofit2.http.*

interface UserRouteService {
    @PUT("users/routes/{userName}/{routeName}")
    suspend fun createUserRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String,
        @Body userRoute: UserRoute
    ): Boolean

    @GET("users/routes/{userName}")
    suspend fun loadUserRoutes(
        @Path("userName") userName: String
    ): List<UserRoute>

    @GET("users/routes/{userName}/{routeName}")
    suspend fun loadUserRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String
    ): UserRoute

    @DELETE("users/routes/{userName}/{routeName}")
    suspend fun deleteUserRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String
    ): Boolean

    @PUT("users/routes/edit/{userName}/{routeName}")
    suspend fun editUserRoute(
        @Path("userName") userName: String,
        @Path("routeName") oldRouteName: String,
        @Body route: EditedUserRoute
    ): Boolean

    /**
     * Lists all the routes' name and associated user name.
     */
    @GET("users/routes/")
    suspend fun listUserRoutes(): List<BrowseListItem>
}