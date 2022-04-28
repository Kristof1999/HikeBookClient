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
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class MyMapListFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkDisplay() {
        launchFragmentInHiltContainer<MyMapListFragment>()

        onView(withId(R.id.switchToMyMapButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.switch_to_my_map_button_text)))
        onView(withId(R.id.myMapRecyclerView))
            .check(matches(isDisplayed()))
    }

    fun checkList() {

    }
}