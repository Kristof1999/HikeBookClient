package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.UserAuth
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @PUT("routes/create/{userName}/{routeName}")
    suspend fun createRoute(
        @Path("userName") userName: String,
        @Path("routeName") routeName: String,
        @Body points: List<Point>
    ) : Boolean
}
