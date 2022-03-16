package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.UserAuth

interface IAuthRepository {
    suspend fun register(user: UserAuth): Boolean
    suspend fun login(user: UserAuth): Boolean
}