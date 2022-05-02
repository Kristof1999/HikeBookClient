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
import hu.kristof.nagy.hikebookclient.data.IGroupsRepository
import hu.kristof.nagy.hikebookclient.di.GroupsRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

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
    val groupsRepository: IGroupsRepository = Mockito.mock(IGroupsRepository::class.java)

    @Test
    fun checkDisplay() {
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