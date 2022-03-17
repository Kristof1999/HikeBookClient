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

package hu.kristof.nagy.hikebookclient.authentication

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.hikebookclient.R
import hu.kristof.nagy.hikebookclient.view.authentication.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// TODO: make it work
@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    // Scope: app
    // Fidelity: high - requires running server
    @Test
    fun loginCorrect() {
        onView(withId(R.id.nameEditText)).check(matches(isDisplayed()))
        /*
        // GIVEN - On the start screen, and the user has registered before

        // WHEN - Logging in
        onView(withId(R.id.nameEditText)).perform(typeText("asd"))
        onView(withId(R.id.passwordEditText)).perform(typeText("password"))
        onView(withId(R.id.loginButton)).perform(click())

        // THEN - Verify that we navigate to MainActivity
        // TODO: Verify that we navigate to MainActivity
        onView(withId(R.id.myMap)).check(matches(isDisplayed()))*/
    }
}