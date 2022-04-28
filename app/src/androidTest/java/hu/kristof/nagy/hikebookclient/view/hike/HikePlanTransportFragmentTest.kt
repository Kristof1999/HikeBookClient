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
class HikePlanTransportFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    fun checkDisplay() {
        launchFragmentInHiltContainer<HikePlanTransportFragment>()

        onView(withId(R.id.hikePlanTransportStartSwitch))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.hike_plan_transport_start_switch_text)))
        onView(withId(R.id.hikePlanTransportEndSwitch))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.hike_plan_transport_end_switch_text)))
        onView(withId(R.id.hikePlanTransportStartButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.transport_start_button_text)))
        onView(withId(R.id.hikePlanTransportMap))
            .check(matches(isDisplayed()))
        onView(withId(R.id.hikePlanTransportTransportMeanSpinner))
            .check(matches(isDisplayed()))
    }

    fun checkSwitches() {

    }

    fun checkSpinner() {

    }
}