package hu.kristof.nagy.hikebookclient.data.repository

import hu.kristof.nagy.hikebookclient.di.Service
import javax.inject.Inject

class HikeRepository @Inject constructor(
    private val service: Service
) : IHikeRepository {
    override suspend fun updateAvgSpeed(userName: String, avgSpeed: Double) {
        service.updateAvgSpeed(userName, avgSpeed)
    }
}