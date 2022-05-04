package hu.kristof.nagy.hikebookclient.view.groups

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import hu.kristof.nagy.hikebookclient.view.groups.detail.GroupsDetailFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class GroupsDetailFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    @Test
    fun checkDisplay() {
        val groupName = "group"
        val isConnectedPage = false
        val bundle = bundleOf(
            Constants.GROUP_NAME_BUNDLE_KEY to groupName,
            Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage
        )
        launchFragmentInHiltContainer<GroupsDetailFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.groupsDetailGroupNameTv))
            .check(matches(isDisplayed()))
            .check(matches(withText(groupName)))
        onView(withId(R.id.groupsDetailGeneralConnectButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.join_text)))
        onView(withId(R.id.groupsDetailViewPager)).check(matches(isDisplayed()))
        onView(withId(R.id.groupsDetailBottomNav)).check(matches(isDisplayed()))
    }
}