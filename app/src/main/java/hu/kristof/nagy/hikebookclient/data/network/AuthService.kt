package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("login")
    suspend fun login(@Body user: User): ServerResponseResult<Boolean>

    @POST("register")
    suspend fun register(@Body user: User): ServerResponseResult<Boolean>
}