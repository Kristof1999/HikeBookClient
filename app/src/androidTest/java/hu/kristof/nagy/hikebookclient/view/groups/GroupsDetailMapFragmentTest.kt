package hu.kristof.nagy.hikebookclient.view.groups

import androidx.core.os.bundleOf
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
import hu.kristof.nagy.hikebookclient.data.routes.IGroupRouteRepository
import hu.kristof.nagy.hikebookclient.di.GroupRouteRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import hu.kristof.nagy.hikebookclient.view.groups.detail.GroupsDetailFragmentDirections
import hu.kristof.nagy.hikebookclient.view.groups.detail.GroupsDetailMapFragment
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@UninstallModules(GroupRouteRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class GroupsDetailMapFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    @Mock
    @BindValue
    lateinit var groupRouteRepository: IGroupRouteRepository

    @Test
    fun checkDisplay() {
        val groupName = "group"
        val isConnectedPage = true
        val bundle = bundleOf(
            Constants.GROUP_NAME_BUNDLE_KEY to groupName,
            Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage
        )
        groupRouteRepository = mock {
            onBlocking {
                loadGroupRoutes(groupName)
            } doReturn ServerResponseResult(true, null, listOf())
        }
        launchFragmentInHiltContainer<GroupsDetailMapFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.groupsMapMap)).check(matches(isDisplayed()))
        onView(withId(R.id.groupsMapCreateRouteFab))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withContentDescription(R.string.create_route_image_description)))
        onView(withId(R.id.groupsMapAddFromMyMapButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.groups_add_from_my_map_button_text)))
    }

    @Test
    fun checkHiddenViews() {
        val groupName = "group"
        val isConnectedPage = false
        val bundle = bundleOf(
            Constants.GROUP_NAME_BUNDLE_KEY to groupName,
            Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage
        )
        groupRouteRepository = mock {
            onBlocking {
                loadGroupRoutes(groupName)
            } doReturn ServerResponseResult(true, null, listOf())
        }
        launchFragmentInHiltContainer<GroupsDetailMapFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.groupsMapCreateRouteFab)).check(matches(not(isDisplayed())))
        onView(withId(R.id.groupsMapAddFromMyMapButton)).check(matches(not(isDisplayed())))
    }

    @Test
    fun verifyRouteCreateNav() {
        val groupName = "group"
        val isConnectedPage = true
        val bundle = bundleOf(
            Constants.GROUP_NAME_BUNDLE_KEY to groupName,
            Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage
        )
        groupRouteRepository = mock {
            onBlocking {
                loadGroupRoutes(groupName)
            } doReturn ServerResponseResult(true, null, listOf())
        }
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<GroupsDetailMapFragment>(bundle, dataBindingIdlingResource) {
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.groupsMapCreateRouteFab)).perform(click())

        val routeType = RouteType.GROUP
        val directions = GroupsDetailFragmentDirections
            .actionGroupsDetailFragmentToRouteCreateFragment(routeType, groupName)
        verify(navController).navigate(directions)
    }
}