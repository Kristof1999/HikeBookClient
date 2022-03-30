package hu.kristof.nagy.hikebookclient.data.network

import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface HikeService {
    @PUT("avgSpeed/{userName}")
    suspend fun updateAvgSpeed(
        @Path("userName") userName: String,
        @Body avgSpeed: Double
    )
}