package hu.kristof.nagy.hikebookclient.view.authentication

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StartFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkDisplay() {
        launchFragmentInHiltContainer<StartFragment>()

        onView(withId(R.id.aboutPageButton)).check(matches(isDisplayed()))
        onView(withId(R.id.aboutPageButton)).check(matches(withText(R.string.about_button_text)))
        onView(withId(R.id.aboutPageButton)).check(matches(isClickable()))
        onView(withId(R.id.nameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.nameEditText)).check(matches(withHint(R.string.user_name_hint)))
        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordEditText)).check(matches(withHint(R.string.password_hint)))
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).check(matches(withText(R.string.login_btn_text)))
        onView(withId(R.id.loginButton)).check(matches(isClickable()))
        onView(withId(R.id.startRegistrationButton)).check(matches(isDisplayed()))
        onView(withId(R.id.startRegistrationButton)).check(matches(withText(R.string.registration_btn_text)))
        onView(withId(R.id.startRegistrationButton)).check(matches(isClickable()))
    }

    @Test
    fun checkEditTexts() {
        launchFragmentInHiltContainer<StartFragment>()
        val userName = "asd"
        val password = "password"

        onView(withId(R.id.nameEditText)).perform(typeText(userName))
        onView(withId(R.id.passwordEditText)).perform(typeText(password))

        onView(withId(R.id.nameEditText)).check(matches(withText(userName)))
        onView(withId(R.id.passwordEditText)).check(matches(withText(password)))
    }

    @Test
    fun testNavToRegister() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<StartFragment> {
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.startRegistrationButton)).perform(click())

        verify(navController).navigate(R.id.action_startFragment_to_registrationFragment)
    }

    @Test
    fun testNavToAbout() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<StartFragment> {
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.aboutPageButton)).perform(click())

        verify(navController).navigate(R.id.action_startFragment_to_aboutFragment)
    }
}