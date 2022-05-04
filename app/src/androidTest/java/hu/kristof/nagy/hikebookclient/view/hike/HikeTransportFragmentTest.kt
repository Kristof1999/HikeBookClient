package hu.kristof.nagy.hikebookclient.view.hike

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
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class HikeTransportFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkDisplay() {
        val p = Constants.START_POINT
        val startPoint = Point(p.latitude, p.longitude, MarkerType.SET, "")
        val endPoint = Point(p.latitude, p.longitude, MarkerType.NEW, "")
        val transportType = TransportType.BICYCLE
        val isForward = true
        val routeName = "route"
        val bundle = HikeTransportFragmentArgs(
            startPoint, endPoint, transportType, isForward, routeName
        ).toBundle()
        launchFragmentInHiltContainer<HikeTransportFragment>(bundle)

        onView(withId(R.id.hikeTransportMap))
            .check(matches(isDisplayed()))
        onView(withId(R.id.hikeTransportFinishButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.transport_finish_button_text)))
    }

    @Test
    fun verifyForwardNav() {
        val navController = mock(NavController::class.java)
        val p = Constants.START_POINT
        val startPoint = Point(p.latitude, p.longitude, MarkerType.SET, "")
        val endPoint = Point(p.latitude, p.longitude, MarkerType.NEW, "")
        val transportType = TransportType.BICYCLE
        val isForward = true
        val routeName = "route"
        val bundle = HikeTransportFragmentArgs(
            startPoint, endPoint, transportType, isForward, routeName
        ).toBundle()
        launchFragmentInHiltContainer<HikeTransportFragment>(bundle) {
            Navigation.setViewNavController(view!!, navController)
        }

        onView(withId(R.id.hikeTransportFinishButton)).perform(click())

        val directions = HikeTransportFragmentDirections
            .actionHikeTransportFragmentToHikeFragment(routeName)
        verify(navController).navigate(directions)
    }

    @Test
    fun verifyBackwardNav() {
        val navController = mock(NavController::class.java)
        val p = Constants.START_POINT
        val startPoint = Point(p.latitude, p.longitude, MarkerType.SET, "")
        val endPoint = Point(p.latitude, p.longitude, MarkerType.NEW, "")
        val transportType = TransportType.BICYCLE
        val isForward = false
        val routeName = "route"
        val bundle = HikeTransportFragmentArgs(
            startPoint, endPoint, transportType, isForward, routeName
        ).toBundle()
        launchFragmentInHiltContainer<HikeTransportFragment>(bundle) {
            Navigation.setViewNavController(view!!, navController)
        }

        onView(withId(R.id.hikeTransportFinishButton)).perform(click())

        verify(navController).navigate(R.id.action_hikeTransportFragment_to_myMapFragment)
    }
}