package hu.kristof.nagy.hikebookclient.view.hike

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.repository.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.di.UserRouteRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import hu.kristof.nagy.hikebookclient.view.routes.MarkerType
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@UninstallModules(UserRouteRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class HikePlanTransportFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    private val routeName = "route"
    private val route = Route.UserRoute("user", routeName, listOf(
        Point(0.0, 0.0, MarkerType.SET, ""),
        Point(0.0, 0.0, MarkerType.NEW, "")
    ), "")
    @BindValue
    val repository = mock<IUserRouteRepository> {
        onBlocking {
            loadUserRouteOfLoggedInUser(routeName)
        } doReturn flowOf(ServerResponseResult(true, null, route))
    }

    @Test
    fun checkDisplay() {
        val isForward = true
        val bundle = HikePlanTransportFragmentArgs(isForward, routeName).toBundle()
        launchFragmentInHiltContainer<HikePlanTransportFragment>(bundle, dataBindingIdlingResource)

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

    @Test
    fun checkSwitches() {
        val isForward = true
        val bundle = HikePlanTransportFragmentArgs(isForward, routeName).toBundle()
        launchFragmentInHiltContainer<HikePlanTransportFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.hikePlanTransportStartSwitch)).check(matches(isNotChecked()))
        onView(withId(R.id.hikePlanTransportEndSwitch)).check(matches(isNotChecked()))

        onView(withId(R.id.hikePlanTransportStartSwitch)).perform(click())

        onView(withId(R.id.hikePlanTransportStartSwitch)).check(matches(isChecked()))
        onView(withId(R.id.hikePlanTransportEndSwitch)).check(matches(isNotChecked()))

        onView(withId(R.id.hikePlanTransportEndSwitch)).perform(click())

        onView(withId(R.id.hikePlanTransportStartSwitch)).check(matches(isNotChecked()))
        onView(withId(R.id.hikePlanTransportEndSwitch)).check(matches(isChecked()))

        onView(withId(R.id.hikePlanTransportEndSwitch)).perform(click())

        onView(withId(R.id.hikePlanTransportStartSwitch)).check(matches(isNotChecked()))
        onView(withId(R.id.hikePlanTransportEndSwitch)).check(matches(isNotChecked()))
    }

    @Test
    fun verifyNav() {
        val navController = Mockito.mock(NavController::class.java)
        val isForward = true
        val bundle = HikePlanTransportFragmentArgs(isForward, routeName).toBundle()
        launchFragmentInHiltContainer<HikePlanTransportFragment>(
            bundle,
            dataBindingIdlingResource
        ) {
            Navigation.setViewNavController(view!!, navController)
        }

        onView(withId(R.id.hikePlanTransportStartButton)).perform(click())

        val p = Constants.START_POINT
        val startPoint = Point(p.latitude, p.longitude, MarkerType.SET, "")
        val endPoint = Point(p.latitude, p.longitude, MarkerType.NEW, "")
        val transportType = TransportType.BICYCLE
        val directions = HikePlanTransportFragmentDirections
            .actionHikePlanFragmentToHikeTransportFragment(
                startPoint, endPoint, transportType, isForward, routeName
            )
        verify(navController).navigate(directions)
    }
}