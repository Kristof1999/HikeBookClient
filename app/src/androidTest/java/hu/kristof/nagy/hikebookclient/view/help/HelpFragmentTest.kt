package hu.kristof.nagy.hikebookclient.view.help

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
class HelpFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkTv() {
        val bundle = HelpFragmentArgs(HelpRequestType.HIKE).toBundle()
        launchFragmentInHiltContainer<HelpFragment>(bundle)

        onView(withId(R.id.helpTextView))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.help_hike)))

        val bundle2 = HelpFragmentArgs(HelpRequestType.MY_MAP).toBundle()
        launchFragmentInHiltContainer<HelpFragment>(bundle2)

        onView(withId(R.id.helpTextView))
            .check(matches(withText(R.string.help_my_map)))
    }
}