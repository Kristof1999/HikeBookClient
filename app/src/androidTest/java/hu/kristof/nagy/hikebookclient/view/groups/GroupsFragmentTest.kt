package hu.kristof.nagy.hikebookclient.view.groups

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.ViewPagerActions
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
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@UninstallModules(GroupsRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class GroupsFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    @BindValue
    lateinit var groupsRepository: IGroupsRepository

    @Test
    fun checkDisplay() {
        groupsRepository = mock {
            onBlocking {
                listGroupsForLoggedInUser(false)
            } doReturn flowOf(ServerResponseResult(true, null, listOf()))
        }
        launchFragmentInHiltContainer<GroupsFragment>(
            dataBindingIdlingResource = dataBindingIdlingResource
        )

        onView(withId(R.id.groupsGroupCreateButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.groups_create_group_button_text)))
        onView(withId(R.id.groupsViewPager)).check(matches(isDisplayed()))
        onView(withId(R.id.groupsTabLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun checkNotConnectedPageDisplayed() {
        groupsRepository = mock {
            onBlocking {
                listGroupsForLoggedInUser(false)
            } doReturn flowOf(ServerResponseResult(true, null, listOf()))
        }
        launchFragmentInHiltContainer<GroupsFragment>(
            dataBindingIdlingResource = dataBindingIdlingResource
        )

        onView(withId(R.id.groupsViewPager)).perform(
            ViewPagerActions.scrollToPage(0)
        )

        onView(withText(R.string.tab_unconnected_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkConnectedPageDisplayed() {
        groupsRepository = mock {
            onBlocking {
                listGroupsForLoggedInUser(true)
            } doReturn flowOf(ServerResponseResult(true, null, listOf()))

            onBlocking {
                listGroupsForLoggedInUser(false)
            } doReturn flowOf(ServerResponseResult(true, null, listOf()))
        }
        launchFragmentInHiltContainer<GroupsFragment>(
            dataBindingIdlingResource = dataBindingIdlingResource
        )

        onView(withId(R.id.groupsViewPager)).perform(
            ViewPagerActions.scrollToPage(1)
        )

        onView(withText(R.string.tab_connected_text))
            .check(matches(isDisplayed()))
    }
}