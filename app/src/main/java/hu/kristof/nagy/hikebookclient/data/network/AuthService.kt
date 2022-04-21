package hu.kristof.nagy.hikebookclient.data.network

import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("login")
    suspend fun login(@Body user: User): ResponseResult<Boolean>

    @POST("register")
    suspend fun register(@Body user: User): ResponseResult<Boolean>
}