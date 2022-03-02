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

package hu.kristof.nagy.hikebookclient.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kristof.nagy.hikebookclient.model.UserAuth
import hu.kristof.nagy.hikebookclient.network.Api
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var name: String = ""
    var pswd: String = ""

    private var _loginRes = MutableLiveData<Boolean>()
    val loginRes : LiveData<Boolean>
        get() = _loginRes

    private val service = Api.retrofitService

    fun onLogin() {
        viewModelScope.launch{
             _loginRes.value = service.login(UserAuth(name, pswd))
        }
    }
}