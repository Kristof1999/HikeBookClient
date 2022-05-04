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

import android.content.Context
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.repository.IAuthRepository
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.User
import hu.kristof.nagy.hikebookclient.util.handleIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.util.handleOffline
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to log in the user.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: IAuthRepository
    ) : ViewModel() {
    private val _loginRes = MutableLiveData<ResponseResult<Boolean>>()
    val loginRes : LiveData<ResponseResult<Boolean>>
        get() = _loginRes

    private var name = ""
    private var password = ""

    // TODO: update javadoc
    /**
     * Calls the data layer to log in the user,
     * after encrypting the user's password,
     * and notifies the view layer of the result.
     * @param user the user to log in
     */
    fun onLogin(context: Context) {
        viewModelScope.launch {
            handleIllegalStateAndArgument(_loginRes) {
                handleOffline(_loginRes, context) {
                    val user = User(name, password)
                    _loginRes.value = repository.login(
                        user.apply { encryptPassword() }
                    )
                }
            }
        }
    }

    fun afterNameChanged(text: Editable) {
        name = text.toString()
    }

    fun afterPasswordChanged(text: Editable) {
        password = text.toString()
    }
}