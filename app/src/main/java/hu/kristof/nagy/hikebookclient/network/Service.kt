package hu.kristof.nagy.hikebookclient.network

import hu.kristof.nagy.hikebookclient.model.UserAuth
import retrofit2.http.Body
import retrofit2.http.POST

interface Service {
    @POST("login")
    suspend fun login(@Body user: UserAuth): Boolean

    @POST("register")
    suspend fun register(@Body user: UserAuth): Boolean
}
