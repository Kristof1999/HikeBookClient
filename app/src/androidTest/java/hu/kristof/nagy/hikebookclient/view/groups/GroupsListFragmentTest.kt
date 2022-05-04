package hu.kristof.nagy.hikebookclient.view.groups

import androidx.core.os.bundleOf
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
import hu.kristof.nagy.hikebookclient.data.repository.IGroupsRepository
import hu.kristof.nagy.hikebookclient.di.GroupsRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@UninstallModules(GroupsRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class GroupsListFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    @Mock
    @BindValue
    lateinit var groupsRepository: IGroupsRepository

    @Test
    fun checkList() {
        val isConnectedPage = true
        val group1 = "group1"
        val group2 = "group2"
        groupsRepository = mock {
            onBlocking {
                listGroupsForLoggedInUser(isConnectedPage)
            } doReturn flowOf(
                ServerResponseResult(true, null, listOf(
                    group1, group2
                ))
            )
        }
        val bundle = bundleOf(Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage)
        launchFragmentInHiltContainer<GroupsListFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.groupsRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsListItemGroupNameTv)))))
            .check(matches(hasDescendant(withChild(withText(group1)))))
            .check(matches(hasDescendant(withChild(withText(group2)))))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsListItemButton)))))
            .check(matches(hasDescendant(withChild(withText(R.string.leave_text)))))
            .check(matches(hasDescendant(withChild(isClickable()))))
    }

    @Test
    fun checkNotConnectedPageGeneralConnectButton() {
        val isConnectedPage = false
        val group1 = "group1"
        groupsRepository = mock {
            onBlocking {
                listGroupsForLoggedInUser(isConnectedPage)
            } doReturn flowOf(
                ServerResponseResult(true, null, listOf(group1))
            )
        }
        val bundle = bundleOf(Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage)
        launchFragmentInHiltContainer<GroupsListFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.groupsListItemButton))
            .check(matches(withText(R.string.join_text)))
    }

    @Test
    fun verifyDetailNav() {
        val isConnectedPage = true
        val group1 = "group1"
        val group2 = "group2"
        groupsRepository = mock {
            onBlocking {
                listGroupsForLoggedInUser(isConnectedPage)
            } doReturn flowOf(
                ServerResponseResult(true, null, listOf(
                    group1, group2
                ))
            )
        }
        val bundle = bundleOf(Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage)
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<GroupsListFragment>(bundle, dataBindingIdlingResource) {
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.groupsRecyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GroupsListAdapter.ViewHolder>(0, click())
        )
        val directions1 = GroupsFragmentDirections
            .actionGroupsFragmentToGroupsDetailFragment(group1, isConnectedPage)
        verify(navController).navigate(directions1)

        onView(withId(R.id.groupsRecyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GroupsListAdapter.ViewHolder>(1, click())
        )
        val directions2 = GroupsFragmentDirections
            .actionGroupsFragmentToGroupsDetailFragment(group2, isConnectedPage)
        verify(navController).navigate(directions2)
    }
}