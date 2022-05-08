package hu.kristof.nagy.hikebookclient.view.mymap

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
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
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
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
class MyMapListFragmentTest {
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
            loadUserRoutesOfLoggedInUser()
        } doReturn flowOf(ServerResponseResult(true, null, listOf(route)))
    }

    @Test
    fun checkDisplay() {
        launchFragmentInHiltContainer<MyMapListFragment>()

        onView(withId(R.id.switchToMyMapButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.switch_to_my_map_button_text)))
        onView(withId(R.id.myMapRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withChild(withId(R.id.myMapListItemRouteName)))))
            .check(matches(hasDescendant(withChild(withText(routeName)))))
            .check(matches(hasDescendant(withChild(withId(R.id.myMapListItemDeleteImageButton)))))
            .check(matches(hasDescendant(withChild(withId(R.id.myMapListItemEditImageButton)))))
            .check(matches(hasDescendant(withChild(withId(R.id.myMapListItemPrintImageButton)))))
            .check(matches(hasDescendant(withChild(withId(R.id.myMapListItemHikePlanButton)))))
            .check(matches(hasDescendant(withChild(withId(R.id.myMapListItemGroupHikeCreateButton)))))
        onView(withId(R.id.myMapListItemDeleteImageButton))
            .check(matches(isClickable()))
            .check(matches(withContentDescription(R.string.delete_image_description)))
        onView(withId(R.id.myMapListItemEditImageButton))
            .check(matches(isClickable()))
            .check(matches(withContentDescription(R.string.edit_image_description)))
        onView(withId(R.id.myMapListItemPrintImageButton))
            .check(matches(isClickable()))
            .check(matches(withContentDescription(R.string.printer_image_description)))
        onView(withId(R.id.myMapListItemHikePlanButton))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.hike_plan_button_text)))
        onView(withId(R.id.myMapListItemGroupHikeCreateButton))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.group_hike_create_text)))
    }

    @Test
    fun verifyNav() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<MyMapListFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.switchToMyMapButton)).perform(click())
        verify(navController).navigate(R.id.action_myMapListFragment_to_myMapFragment)

        onView(withId(R.id.myMapListItemEditImageButton)).perform(click())
        val directions = MyMapListFragmentDirections
            .actionMyMapListFragmentToRouteEditFragment(RouteType.USER, null, routeName)
        verify(navController).navigate(directions)

        onView(withId(R.id.myMapListItemPrintImageButton)).perform(click())
        val detailDirections = MyMapListFragmentDirections
            .actionMyMapListFragmentToMyMapDetailFragment(routeName)
        verify(navController).navigate(detailDirections)

        onView(withId(R.id.myMapListItemHikePlanButton)).perform(click())
        val hikePlanDirections = MyMapListFragmentDirections
            .actionMyMapListFragmentToHikePlanDateFragment(routeName)
        verify(navController).navigate(hikePlanDirections)
    }

    @Test
    fun verifyGroupHikeNav() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<MyMapListFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.myMapListItemGroupHikeCreateButton)).perform(click())

        val detailDirections = MyMapListFragmentDirections
            .actionMyMapListFragmentToMyMapDetailFragment(routeName)
        verify(navController).navigate(detailDirections)
    }

    @Test
    fun verifyDetailNav() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<MyMapListFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.myMapRecyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MyMapListAdapter.ViewHolder>(0, click())
        )

        val detailDirections = MyMapListFragmentDirections
            .actionMyMapListFragmentToMyMapDetailFragment(routeName)
        verify(navController).navigate(detailDirections)
    }
}