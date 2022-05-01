package hu.kristof.nagy.hikebookclient.view.grouphike

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
import hu.kristof.nagy.hikebookclient.data.IGroupHikeRepository
import hu.kristof.nagy.hikebookclient.di.GroupHikeRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.GroupHikeListHelper
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
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

@UninstallModules(GroupHikeRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class GroupHikeListFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Mock
    @BindValue
    lateinit var groupHikeRepository: IGroupHikeRepository

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    @Test
    fun checkList() {
        val groupHikeName1 = "groupHike1"
        val groupHikeName2 = "groupHike2"
        val dateTime = DateTime(2022, 5, 1, 12, 0)
        groupHikeRepository = mock {
            onBlocking {
                listGroupHikesForLoggedInUser(false)
            } doReturn flowOf(
                ServerResponseResult(true, null, listOf(
                    GroupHikeListHelper(groupHikeName1, dateTime),
                    GroupHikeListHelper(groupHikeName2, dateTime)
                ))
            )
        }
        val bundle = bundleOf(GroupHikeListFragment.IS_CONNECTED_PAGE_BUNDLE_KEY to false)
        launchFragmentInHiltContainer<GroupHikeListFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.groupHikeListRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withChild(withId(R.id.groupHikeListItemGroupHikeNameTv)))))
            .check(matches(hasDescendant(withChild(withText(groupHikeName1)))))
            .check(matches(hasDescendant(withChild(withText(groupHikeName2)))))
            .check(matches(hasDescendant(withChild(withId(R.id.groupHikeListItemDateTv)))))
            .check(matches(hasDescendant(withChild(withText(dateTime.toString())))))
            .check(matches(hasDescendant(withChild(withId(R.id.groupHikeListItemGeneralConnectButton)))))
            .check(matches(hasDescendant(withChild(withText("Csatlakozás")))))
    }

    @Test
    fun verifyNav() {
        val groupHikeName1 = "groupHike1"
        val isConnectedPage = false
        val dateTime = DateTime(2022, 5, 1, 12, 0)
        groupHikeRepository = mock {
            onBlocking {
                listGroupHikesForLoggedInUser(isConnectedPage)
            } doReturn flowOf(
                ServerResponseResult(true, null, listOf(
                    GroupHikeListHelper(groupHikeName1, dateTime)
                ))
            )
        }
        val bundle = bundleOf(GroupHikeListFragment.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage)
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<GroupHikeListFragment>(bundle, dataBindingIdlingResource) {
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.groupHikeListRecyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GroupHikeListAdapter.ViewHolder>(0, click())
        )

        val directions = GroupHikeFragmentDirections
            .actionGroupHikeFragmentToGroupHikeDetailFragment(
                groupHikeName1,
                isConnectedPage,
                dateTime
            )
        verify(navController).navigate(directions)
    }
}