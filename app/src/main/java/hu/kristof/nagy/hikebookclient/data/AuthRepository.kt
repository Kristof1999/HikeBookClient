package hu.kristof.nagy.hikebookclient.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.User
import hu.kristof.nagy.hikebookclient.util.Constants
import javax.inject.Inject

/**
 * An IAuthRepository that stores the user's name
 * in a datastore if the authentication was successful.
 */
class AuthRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
    ) : IAuthRepository {

    override suspend fun register(user: User): ResponseResult<Boolean> {
        val responseResult = service.register(user)
        if (responseResult.isSuccess) {
            if (responseResult.successResult!!) {
                dataStore.edit { data ->
                    data[Constants.DATA_STORE_USER_NAME] = user.name
                }
                return ResponseResult(true, null, true)
            } else {
                return ResponseResult(true, null, false)
            }
        } else {
            return responseResult
        }
    }

    override suspend fun login(user: User): ResponseResult<Boolean> {
        val responseResult = service.login(user)
        if (responseResult.isSuccess) {
            if(responseResult.successResult!!) {
                dataStore.edit { data ->
                    data[Constants.DATA_STORE_USER_NAME] = user.name
                }
                return ResponseResult(true, null, true)
            } else {
                return ResponseResult(true, null, false)
            }
        } else {
            return responseResult
        }
    }
}