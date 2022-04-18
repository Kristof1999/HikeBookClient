package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.User

class DummyAuthRepository : IAuthRepository {
    override suspend fun register(user: User): Boolean {
        return true
    }

    override suspend fun login(user: User): Boolean {
        return true
    }

}