package hu.kristof.nagy.hikebookclient.service

import hu.kristof.nagy.hikebookclient.model.UserAuth
import hu.kristof.nagy.hikebookclient.network.Service

class DummyService : Service {
    override suspend fun login(user: UserAuth): Boolean {
        return true
    }

    override suspend fun register(user: UserAuth): Boolean {
        return true
    }
}