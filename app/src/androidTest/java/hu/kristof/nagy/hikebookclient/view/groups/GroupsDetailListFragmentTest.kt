package hu.kristof.nagy.hikebookclient.view.groups

import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
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
import hu.kristof.nagy.hikebookclient.data.routes.IGroupRouteRepository
import hu.kristof.nagy.hikebookclient.di.GroupRouteRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMapViewModel
import org.hamcrest.Matcher
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

    @Mock
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
        val bundle = GroupsDetailListFragmentArgs(groupName, true).toBundle()
        launchFragmentInHiltContainer<GroupsDetailListFragment>(bundle, dataBindingIdlingResource) {
            val mapViewModel: GroupsDetailMapViewModel by activityViewModels()
            mapViewModel.loadRoutesOfGroup(groupName)
        }

        onView(withId(R.id.groupsDetailListRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsDetailListItemRouteNameTv)))))
            .check(matches(hasDescendant(withChild(withText(routeName)))))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsDetailListItemAddToMyMapButton)))))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsDetailListItemDeleteImageButton)))))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsDetailListItemEditImageButton)))))
    }

    fun checkHiddenViews() {

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
        val bundle = GroupsDetailListFragmentArgs(groupName, true).toBundle()
        launchFragmentInHiltContainer<GroupsDetailListFragment>(bundle, dataBindingIdlingResource) {
            val mapViewModel: GroupsDetailMapViewModel by activityViewModels()
            mapViewModel.loadRoutesOfGroup(groupName)
            Navigation.setViewNavController(this.view!!, navController)
        }

        val clickOnEditButton = object : ViewAction {
            override fun getConstraints(): Matcher<View> {
               return isDisplayed()
            }

            override fun getDescription(): String {
                return "performing click on editImageButton"
            }

            override fun perform(uiController: UiController?, view: View?) {
                val editButton = view!!.findViewById<AppCompatImageButton>(
                    R.id.groupsDetailListItemEditImageButton
                )
                editButton.performClick()
            }
        }
        onView(withId(R.id.groupsDetailListRecyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GroupsDetailListAdapter.ViewHolder>(0, clickOnEditButton)
        )

        val directions = GroupsDetailFragmentDirections
            .actionGroupsDetailFragmentToRouteEditFragment(RouteType.GROUP, groupName, routeName)
        verify(navController).navigate(directions)
    }
}