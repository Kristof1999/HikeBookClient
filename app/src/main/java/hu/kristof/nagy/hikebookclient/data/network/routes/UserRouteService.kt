package hu.kristof.nagy.hikebookclient.data.network.routes

import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.EditedUserRoute
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import retrofit2.http.*

interface UserRouteService {
    @PUT("users/routes/{userName}/{routeName}")
    suspend fun createUserRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String,
        @Body userRoute: UserRoute
    ): ResponseResult<Boolean>

    @GET("users/routes/{userName}")
    suspend fun loadUserRoutes(
        @Path("userName") userName: String
    ): ResponseResult<List<UserRoute>>

    @GET("users/routes/{userName}/{routeName}")
    suspend fun loadUserRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String
    ): ResponseResult<UserRoute>

    @DELETE("users/routes/{userName}/{routeName}")
    suspend fun deleteUserRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String
    ): ResponseResult<Boolean>

    @PUT("users/routes/edit/{userName}/{routeName}")
    suspend fun editUserRoute(
        @Path("userName") userName: String,
        @Path("routeName") oldRouteName: String,
        @Body editedUserRoute: EditedUserRoute
    ): ResponseResult<Boolean>

    @GET("users/routes/browse/{userName}")
    suspend fun listUserRoutes(
        @Path("userName") userName: String
    ): ResponseResult<List<BrowseListItem>>
}