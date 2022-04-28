package hu.kristof.nagy.hikebookclient.view.grouphike

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
class GroupHikeDetailFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    fun checkDisplay() {
        // TODO: provide args, ...
        launchFragmentInHiltContainer<GroupHikeDetailFragment>()

        onView(withId(R.id.groupHikeDetailDescriptionTv)).check(matches(isDisplayed()))
        onView(withId(R.id.groupHikeDetailNameTv)).check(matches(isDisplayed()))
        onView(withId(R.id.groupHikeDetailMap)).check(matches(isDisplayed()))
        onView(withId(R.id.groupHikeDetailRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.groupHikeDetailAddToMyMapButton)).check(matches(isDisplayed()))
        onView(withId(R.id.groupHikeDetailAddToMyMapButton)).check(matches(isClickable()))
        onView(withId(R.id.groupHikeDetailGeneralConnectButton)).check(matches(isDisplayed()))
        onView(withId(R.id.groupHikeDetailGeneralConnectButton)).check(matches(isClickable()))
    }

    fun checkGeneralConnectButton() {
        // TODO: check if the generalConnectButton's text
        // is the right one for connected, and not connected cases
    }

    fun checkMembers() {

    }
}