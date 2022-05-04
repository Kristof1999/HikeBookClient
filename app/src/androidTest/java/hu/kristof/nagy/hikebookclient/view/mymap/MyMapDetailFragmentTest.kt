package hu.kristof.nagy.hikebookclient.view.mymap

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
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
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
class MyMapDetailFragmentTest {
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
        onBlocking {
            deleteUserRouteOfLoggedInUser(routeName)
        } doReturn flowOf(ResponseResult.Success(true))
    }

    @Test
    fun checkDisplay() {
        val bundle = MyMapDetailFragmentArgs(routeName).toBundle()
        launchFragmentInHiltContainer<MyMapDetailFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.myMapDetailRouteNameTv))
            .check(matches(isDisplayed()))
            .check(matches(withText(routeName)))
        onView(withId(R.id.myMapDetailMap))
            .check(matches(isDisplayed()))
        onView(withId(R.id.myMapDetailHikePlanFab))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withId(R.id.myMapDetailEditButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.edit_text)))
        onView(withId(R.id.myMapDetailDeleteButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.delete_text)))
        onView(withId(R.id.myMapDetailPrintButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.print_text)))
        onView(withId(R.id.myMapDetailGroupHikeCreateButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.group_hike_create_text)))
    }

    @Test
    fun checkNav() {
        val navController = Mockito.mock(NavController::class.java)
        val bundle = MyMapDetailFragmentArgs(routeName).toBundle()
        launchFragmentInHiltContainer<MyMapDetailFragment>(bundle, dataBindingIdlingResource) {
            Navigation.setViewNavController(view!!, navController)
        }

        onView(withId(R.id.myMapDetailEditButton)).perform(click())
        val directions = MyMapDetailFragmentDirections
            .actionMyMapDetailFragmentToRouteEditFragment(RouteType.USER, null, routeName)
        verify(navController).navigate(directions)

        onView(withId(R.id.myMapDetailHikePlanFab)).perform(click())
        val directions2 = MyMapDetailFragmentDirections
            .actionMyMapDetailFragmentToHikePlanDateFragment(routeName)
        verify(navController).navigate(directions2)

        onView(withId(R.id.myMapDetailDeleteButton)).perform(click())
        verify(navController).navigate(R.id.action_myMapDetailFragment_to_myMapListFragment)
    }
}