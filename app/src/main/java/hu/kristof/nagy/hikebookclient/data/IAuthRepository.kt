package hu.kristof.nagy.hikebookclient.data

import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.User

/**
 * A Repository that helps to register and log in an user.
 */
interface IAuthRepository {
    /**
     * Helps to register the user.
     * @return true if registration was successful
     */
    suspend fun register(user: User): ServerResponseResult<Boolean>
    /**
     * Helps to log in the user.
     * @return true if logging in was successful
     */
    suspend fun login(user: User): ResponseResult<Boolean>
}