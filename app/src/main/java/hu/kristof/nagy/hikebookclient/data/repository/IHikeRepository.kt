package hu.kristof.nagy.hikebookclient.data.repository

interface IHikeRepository {
    suspend fun updateAvgSpeed(userName: String, avgSpeed: Double)
}