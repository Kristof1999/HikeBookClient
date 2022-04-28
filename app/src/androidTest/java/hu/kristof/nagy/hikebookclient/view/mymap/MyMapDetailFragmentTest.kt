package hu.kristof.nagy.hikebookclient.view.mymap

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
class MyMapDetailFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    fun checkDisplay() {
        launchFragmentInHiltContainer<MyMapDetailFragment>()

        onView(withId(R.id.myMapDetailRouteNameTv))
            .check(matches(isDisplayed()))
        onView(withId(R.id.myMapDetailMap))
            .check(matches(isDisplayed()))
        onView(withId(R.id.myMapDetailHikePlanFab))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withId(R.id.myMapDetailEditButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.edit_text)))
        onView(withId(R.id.myMapDetailDeleteButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.delete_text)))
        onView(withId(R.id.myMapDetailPrintButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.print_text)))
        onView(withId(R.id.myMapDetailGroupHikeCreateButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.group_hike_create_text)))
    }
}