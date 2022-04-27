package hu.kristof.nagy.hikebookclient.viewmodel.routes.authentication

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.view.authentication.StartFragment
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class StartFragmentTest {

    @Test
    fun checkDisplay() {
        launchFragmentInContainer<StartFragment>(themeResId = R.style.Theme_HikeBookClient)

        onView(withId(R.id.aboutPageButton)).check(matches(isDisplayed()))
    }
}