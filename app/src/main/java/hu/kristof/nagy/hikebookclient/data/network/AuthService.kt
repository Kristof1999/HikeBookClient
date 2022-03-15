package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.UserAuth
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
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
}