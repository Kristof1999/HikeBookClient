/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// based on:
// https://github.com/googlecodelabs/android-testing

package hu.kristof.nagy.hikebookclient.viewModel.authentication

import hu.kristof.nagy.hikebookclient.data.DummyAuthRepository
import hu.kristof.nagy.hikebookclient.model.UserAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {
    private val loginViewModel = LoginViewModel(DummyAuthRepository())

    @ExperimentalCoroutinesApi
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testEmptyNameOrPassword() {
        var user = UserAuth("", "")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("", "password")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("name", "")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("", "pass")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("user", "password")
        loginViewModel.onLogin(user)
        loginViewModel.loginRes.value?.let { assertTrue(it) }
    }

    @Test
    fun testIllegalName(){
        var user = UserAuth("/", "password")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("  /", "password")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("user/1", "password")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("///", "password")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }
    }

    @Test
    fun testPasswordLength() {
        var user = UserAuth("user", "p")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("user", "pa")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("user", "pas")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("user", "pass")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("user", "passw")
        assertThrows(
            IllegalArgumentException::class.java
        ) { loginViewModel.onLogin(user) }

        user = UserAuth("user", "passwo")
        loginViewModel.onLogin(user)
        loginViewModel.loginRes.value?.let { assertTrue(it) }
    }
}