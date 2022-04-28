package hu.kristof.nagy.hikebookclient.view.groups

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import org.junit.Rule
import org.junit.runner.RunWith

@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class GroupsDetailFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    fun checkDisplay() {
        launchFragmentInHiltContainer<GroupsDetailFragment>()

        onView(withId(R.id.groupsDetailGroupNameTv)).check(matches(isDisplayed()))
        onView(withId(R.id.groupsDetailGeneralConnectButton)).check(matches(isDisplayed()))
        onView(withId(R.id.groupsDetailGeneralConnectButton)).check(matches(isClickable()))
        onView(withId(R.id.groupsDetailNavHostFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.groupsDetailBottomNav)).check(matches(isDisplayed()))
    }

    fun checkGroupNameTv() {

    }

    fun checkGeneralConnectButton() {

    }
}