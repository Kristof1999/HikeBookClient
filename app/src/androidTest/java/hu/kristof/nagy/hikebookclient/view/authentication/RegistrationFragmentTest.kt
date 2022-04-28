package hu.kristof.nagy.hikebookclient.view.authentication

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RegistrationFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkDisplay() {
        launchFragmentInHiltContainer<RegistrationFragment>()

        onView(withId(R.id.registerNameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.registerNameEditText)).check(matches(withHint(R.string.user_name_hint)))
        onView(withId(R.id.registerPasswordEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.registerPasswordEditText)).check(matches(withHint(R.string.password_hint)))
        onView(withId(R.id.registerButton)).check(matches(isDisplayed()))
        onView(withId(R.id.registerButton)).check(matches(withText(R.string.registration_btn_text)))
        onView(withId(R.id.registerButton)).check(matches(isClickable()))
    }

    @Test
    fun checkEditTexts() {
        launchFragmentInHiltContainer<RegistrationFragment>()
        val userName = "asd"
        val password = "password"

        onView(withId(R.id.registerNameEditText)).perform(typeText(userName))
        onView(withId(R.id.registerPasswordEditText)).perform(typeText(password))

        onView(withId(R.id.registerNameEditText)).check(matches(withText(userName)))
        onView(withId(R.id.registerPasswordEditText)).check(matches(withText(password)))
    }
}