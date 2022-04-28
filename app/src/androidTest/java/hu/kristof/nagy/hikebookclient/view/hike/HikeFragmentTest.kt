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
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class HikeFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkDisplay() {
        launchFragmentInHiltContainer<HikeFragment>()

        onView(withId(R.id.hikeOfflineButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.offline_hike_button_text)))
        onView(withId(R.id.hikeStartButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.hike_start_button_text)))
        onView(withId(R.id.hikeFinishButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.hike_finish_button_text)))
        onView(withId(R.id.hikeBackwardsPlanTransportButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.hike_backwards_transport_plan_button_text)))
        onView(withId(R.id.hikeMyLocationFab))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withId(R.id.hikeMap))
            .check(matches(isDisplayed()))
    }
}