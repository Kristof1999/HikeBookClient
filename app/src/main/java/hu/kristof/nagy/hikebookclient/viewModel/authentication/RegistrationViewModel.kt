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
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.IAuthRepository
import hu.kristof.nagy.hikebookclient.model.User
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to register the user.
 */
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: IAuthRepository
    ) : ViewModel() {
    private var _registrationRes = MutableLiveData<Boolean>()
    val registrationRes : LiveData<Boolean>
        get() = _registrationRes

    /**
     * Calls the data layer to register the user,
     * after encrypting the user's password,
     * and notifies the view layer of the result.
     * @param user the user to register
     */
    fun onRegister(user: User) {
        viewModelScope.launch{
            _registrationRes.value = repository.register(
                user.apply { encryptPassword() }
            )
        }
    }
}