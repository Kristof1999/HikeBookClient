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
import hu.kristof.nagy.hikebookclient.model.RouteType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class RouteCreateFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkDisplay() {
        val routeType = RouteType.USER
        val bundle = RouteCreateFragmentArgs(routeType, null).toBundle()
        launchFragmentInHiltContainer<RouteCreateFragment>(bundle)

        onView(withId(R.id.routeCreateRouteNameEditText))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.route_name_hint)))
        onView(withId(R.id.routeCreateHikeDescriptionEditText))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.hike_description_hint)))
        onView(withId(R.id.routeCreateCreateButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.route_create_button_text)))
        onView(withId(R.id.routeCreateDeleteSwitch))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.delete_text)))
        onView(withId(R.id.routeCreateMarkerSpinner))
            .check(matches(isDisplayed()))
        onView(withId(R.id.routeCreateMap))
            .check(matches(isDisplayed()))
    }
}