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
class GroupsDetailMapFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    fun checkDisplay() {
        launchFragmentInHiltContainer<GroupsDetailMapFragment>()

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
}