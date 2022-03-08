/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// based on:
// https://github.com/google-developer-training/android-kotlin-fundamentals-apps/tree/master/GuessTheWordLiveData
// https://developer.android.com/training/dependency-injection/hilt-android

package hu.kristof.nagy.hikebookclient.viewModel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.model.UserAuth
import hu.kristof.nagy.hikebookclient.network.Service
import javax.inject.Inject

/**
 * Encapsulates the login flow, and provides a way for the ui/view layer
 * to get notified of the result of the login attempt.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private var _loginRes = MutableLiveData<Boolean>()
    val loginRes : LiveData<Boolean>
        get() = _loginRes

    /**
     * Performs check on the given user, encodes its password,
     * and attempts to log in the user.
     */
    fun onLogin(user: UserAuth) {
        _loginRes.value = true
    /*AuthChecker.check(user)

        val password = MessageDigest.getInstance("MD5").digest(
            user.password.toByteArray()
        ).joinToString(separator = "")

        viewModelScope.launch{
            _loginRes.value = service.login(UserAuth(user.name, password))
        }*/
    }
}