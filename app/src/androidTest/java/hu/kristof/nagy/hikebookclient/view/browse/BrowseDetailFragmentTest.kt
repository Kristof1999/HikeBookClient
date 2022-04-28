package hu.kristof.nagy.hikebookclient.view.browse

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
class BrowseDetailFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

//    @Inject
//    private lateinit var viewModel: BrowseDetailViewModel

    @Test
    fun checkDisplay() {
        // TODO: use viewModel, make BrowseDetailFragmentArgs (and call .toBundle)
        //       and then check
        // mock userRouteRepository, @BindValue
        launchFragmentInHiltContainer<BrowseDetailFragment>()

        onView(withId(R.id.browseDetailHikeDescriptionTv)).check(matches(isDisplayed()))
        onView(withId(R.id.browseDetailMap)).check(matches(isDisplayed()))
        onView(withId(R.id.browseDetailAddToMyMapButton)).check(matches(isDisplayed()))
        onView(withId(R.id.browseDetailAddToMyMapButton)).check(matches(withText(R.string.add_to_my_map_text)))
        onView(withId(R.id.browseDetailAddToMyMapButton)).check(matches(isClickable()))
    }
}