package hu.kristof.nagy.hikebookclient.view.mymap

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class MyMapFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkDisplay() {
        launchFragmentInContainer<MyMapFragment>()

        onView(withId(R.id.switchToMyMapListButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.switch_to_my_map_list_button_text)))
        onView(withId(R.id.routeCreateFab))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withId(R.id.myMap))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyListViewNav() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<MyMapFragment> {
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.switchToMyMapListButton)).perform(click())

        verify(navController).navigate(R.id.action_myMapFragment_to_myMapListFragment)
    }

    @Test
    fun verifyRouteCreateNav() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<MyMapFragment> {
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.routeCreateFab)).perform(click())

        val routeType = RouteType.USER
        val directions = MyMapFragmentDirections
            .actionMyMapFragmentToRouteCreateFragment(routeType, null)
        verify(navController).navigate(directions)
    }
}