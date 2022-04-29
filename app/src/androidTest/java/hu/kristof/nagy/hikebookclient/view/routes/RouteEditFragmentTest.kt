package hu.kristof.nagy.hikebookclient.view.routes

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
@RunWith(AndroidJUnit4::class)
@MediumTest
class RouteEditFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    fun checkDisplay() {
        launchFragmentInHiltContainer<RouteEditFragment>()


        onView(withId(R.id.routeEditRouteNameEditText))
            .check(matches(isDisplayed()))
            .check(matches(withHint((R.string.route_name_hint))))
        onView(withId(R.id.routeEditHikeDescriptionEditText))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.hike_description_hint)))
        onView(withId(R.id.routeEditEditButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.route_create_button_text)))
        onView(withId(R.id.routeEditDeleteSwitch))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.delete_text)))
        onView(withId(R.id.routeEditSpinner))
            .check(matches(isDisplayed()))
        onView(withId(R.id.routeEditMap))
            .check(matches(isDisplayed()))
    }
}