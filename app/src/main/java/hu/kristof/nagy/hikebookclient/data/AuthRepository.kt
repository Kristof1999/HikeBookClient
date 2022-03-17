package hu.kristof.nagy.hikebookclient.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.UserAuth
import hu.kristof.nagy.hikebookclient.util.Constants
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
    ) : IAuthRepository {
    /**
     * Stores the registered user's name in a datastore
     * if the registration was successful.
     * @param user user's credentials to use for registration
     * @return true if registration was successful
     */
    override suspend fun register(user: UserAuth): Boolean {
        if (service.register(user)) {
            dataStore.edit { data ->
                data[Constants.DATA_STORE_USER_NAME] = user.name
            }
            return true
        } else {
            return false
        }
    }

    /**
     * Stores the logged in user's name in a datastore
     * if the login was successful.
     * @param user user's credentials to use for login
     * @return true if login was successful
     */
    override suspend fun login(user: UserAuth): Boolean {
        if (service.login(user)) {
            dataStore.edit { data ->
                data[Constants.DATA_STORE_USER_NAME] = user.name
            }
            return true
        } else {
            return false
        }
    }
}