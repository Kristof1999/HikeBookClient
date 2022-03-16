package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.UserAuth

class DummyAuthRepository : IAuthRepository {
    override suspend fun register(user: UserAuth): Boolean {
        return true
    }

    override suspend fun login(user: UserAuth): Boolean {
        return true
    }

}