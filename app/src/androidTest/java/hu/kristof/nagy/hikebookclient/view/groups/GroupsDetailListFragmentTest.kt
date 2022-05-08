package hu.kristof.nagy.hikebookclient.view.groups

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
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
import hu.kristof.nagy.hikebookclient.data.repository.routes.IGroupRouteRepository
import hu.kristof.nagy.hikebookclient.di.GroupRouteRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import hu.kristof.nagy.hikebookclient.view.groups.detail.GroupsDetailFragmentDirections
import hu.kristof.nagy.hikebookclient.view.groups.detail.GroupsDetailListFragment
import hu.kristof.nagy.hikebookclient.view.routes.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMapViewModel
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
class GroupsDetailListFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    lateinit var groupRouteRepository: IGroupRouteRepository

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    @Test
    fun checkList() {
        val groupName = "group"
        val routeName = "route"
        val groupRoute = Route.GroupRoute(groupName, routeName, listOf(
            Point(0.0, 0.0, MarkerType.SET, ""),
            Point(0.0, 0.0, MarkerType.NEW, "")
        ), "")
        groupRouteRepository = mock {
            onBlocking {
                loadGroupRoutes(groupName)
            } doReturn ServerResponseResult(true, null, listOf(groupRoute))
        }
        val bundle = bundleOf(
            Constants.GROUP_NAME_BUNDLE_KEY to groupName,
            Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to true
        )
        launchFragmentInHiltContainer<GroupsDetailListFragment>(bundle, dataBindingIdlingResource) {
            val mapViewModel: GroupsDetailMapViewModel by activityViewModels()
            mapViewModel.loadRoutesOfGroup(groupName)
        }

        onView(withId(R.id.groupsDetailListRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsDetailListItemRouteNameTv)))))
            .check(matches(hasDescendant(withChild(withText(routeName)))))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsDetailListItemAddToMyMapButton)))))
            .check(matches(hasDescendant(withChild(withText(R.string.add_to_my_map_text)))))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsDetailListItemDeleteImageButton)))))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsDetailListItemEditImageButton)))))
    }

    @Test
    fun checkHiddenViews() {
        val groupName = "group"
        val routeName = "route"
        val groupRoute = Route.GroupRoute(groupName, routeName, listOf(
            Point(0.0, 0.0, MarkerType.SET, ""),
            Point(0.0, 0.0, MarkerType.NEW, "")
        ), "")
        groupRouteRepository = mock {
            onBlocking {
                loadGroupRoutes(groupName)
            } doReturn ServerResponseResult(true, null, listOf(groupRoute))
        }
        val bundle = bundleOf(
            Constants.GROUP_NAME_BUNDLE_KEY to groupName,
            Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to false
        )
        launchFragmentInHiltContainer<GroupsDetailListFragment>(bundle, dataBindingIdlingResource) {
            val mapViewModel: GroupsDetailMapViewModel by activityViewModels()
            mapViewModel.loadRoutesOfGroup(groupName)
        }

        onView(withId(R.id.groupsDetailListItemAddToMyMapButton)).check(matches(not(isDisplayed())))
        onView(withId(R.id.groupsDetailListItemDeleteImageButton)).check(matches(not(isDisplayed())))
        onView(withId(R.id.groupsDetailListItemEditImageButton)).check(matches(not(isDisplayed())))
    }

    @Test
    fun verifyNav() {
        val groupName = "group"
        val routeName = "route"
        val groupRoute = Route.GroupRoute(groupName, routeName, listOf(
            Point(0.0, 0.0, MarkerType.SET, ""),
            Point(0.0, 0.0, MarkerType.NEW, "")
        ), "")
        groupRouteRepository = mock {
            onBlocking {
                loadGroupRoutes(groupName)
            } doReturn ServerResponseResult(true, null, listOf(groupRoute))
        }
        val navController = Mockito.mock(NavController::class.java)
        val bundle = bundleOf(
            Constants.GROUP_NAME_BUNDLE_KEY to groupName,
            Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to true
        )
        launchFragmentInHiltContainer<GroupsDetailListFragment>(bundle, dataBindingIdlingResource) {
            val mapViewModel: GroupsDetailMapViewModel by activityViewModels()
            mapViewModel.loadRoutesOfGroup(groupName)
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.groupsDetailListItemEditImageButton)).perform(click())

        val directions = GroupsDetailFragmentDirections
            .actionGroupsDetailFragmentToRouteEditFragment(RouteType.GROUP, groupName, routeName)
        verify(navController).navigate(directions)
    }
}