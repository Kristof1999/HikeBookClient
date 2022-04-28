package hu.kristof.nagy.hikebookclient.view.hike

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import org.junit.Rule
import org.junit.runner.RunWith

@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class HikeTransportFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    fun checkDisplay() {
        launchFragmentInHiltContainer<HikeTransportFragment>()

        onView(withId(R.id.hikeTransportMap))
            .check(matches(isDisplayed()))
        onView(withId(R.id.hikeTransportFinishButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.transport_finish_button_text)))
    }
}