package hu.kristof.nagy.hikebookclient.view.hike

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.longClick
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
import hu.kristof.nagy.hikebookclient.view.routes.MarkerType
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@UninstallModules(UserRouteRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class HikeFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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
        val bundle = HikeFragmentArgs(routeName).toBundle()
        launchFragmentInHiltContainer<HikeFragment>(bundle)

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

    @Test
    fun verifyNav() {
        val bundle = HikeFragmentArgs(routeName).toBundle()
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<HikeFragment>(bundle) {
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.hikeFinishButton)).perform(longClick())
        verify(navController).navigate(R.id.action_hikeFragment_to_myMapFragment)

        onView(withId(R.id.hikeBackwardsPlanTransportButton)).perform(longClick())
        val isForward = false
        val directions = HikeFragmentDirections
            .actionHikeFragmentToHikePlanTransportFragment(isForward, routeName)
        verify(navController).navigate(directions)
    }
}