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
// https://developer.android.com/topic/libraries/architecture/datastore

package hu.kristof.nagy.hikebookclient.viewModel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.IAuthRepository
import hu.kristof.nagy.hikebookclient.model.UserAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to log in the user.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: IAuthRepository
    ) : ViewModel() {
    private var _loginRes = MutableLiveData<Boolean>()
    val loginRes : LiveData<Boolean>
        get() = _loginRes

    /**
     * Calls the data layer to log in the user,
     * after encrypting the user's password,
     * and notifies the view layer of the result.
     * @param user the user to log in
     */
    fun onLogin(user: UserAuth) {
        viewModelScope.launch {
            _loginRes.value = repository.login(
                user.apply { encryptPassword() }
            )
        }
    }
}