package hu.kristof.nagy.hikebookclient.view.browse

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.repository.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.di.UserRouteRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@UninstallModules(UserRouteRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class BrowseDetailFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Mock
    @BindValue
    lateinit var userRouteRepository: IUserRouteRepository

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    // note for running this test:
    // run it when the target device is online,
    // because see BrowseDetailFragment setupLoad:
    // handleOfflineLoad won't load if the device is offline
    @Test
    fun checkDisplay() {
        val userName = "asd"
        val routeName = "route"
        val description = "description"
        val userRoute = Route.UserRoute(userName, routeName, listOf(
            Point(0.0, 0.0, MarkerType.SET, ""),
            Point(0.0, 0.0, MarkerType.NEW, "")
        ), description)
        userRouteRepository = mock {
            onBlocking {
                loadUserRouteOfUser(userName, routeName)
            } doReturn ServerResponseResult(true, null, userRoute)
        }
        val bundle = BrowseDetailFragmentArgs(userName, routeName).toBundle()
        launchFragmentInHiltContainer<BrowseDetailFragment>(bundle, dataBindingIdlingResource)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val descriptionTvText = context.getString(
            R.string.browse_hike_detail_description,
            userName, routeName, description
        )

        onView(withId(R.id.browseDetailHikeDescriptionTv))
            .check(matches(isDisplayed()))
            .check(matches(withText(descriptionTvText)))
        onView(withId(R.id.browseDetailMap))
            .check(matches(isDisplayed()))
        onView(withId(R.id.browseDetailAddToMyMapButton))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.add_to_my_map_text)))
            .check(matches(isClickable()))
    }
}