package hu.kristof.nagy.hikebookclient.view.routes

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
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@UninstallModules(UserRouteRepositoryModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@MediumTest
class RouteEditFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    lateinit var userRouteRepository: IUserRouteRepository

    @Test
    fun checkDisplay() {
        val routeType = RouteType.USER
        val routeName = "route"
        val userRoute = Route.UserRoute("user", routeName, listOf(
            Point(0.0, 0.0, MarkerType.SET, ""),
            Point(0.0, 0.0, MarkerType.NEW, "")
        ), "")
        userRouteRepository = mock {
            onBlocking {
                loadUserRouteOfLoggedInUser(routeName)
            } doReturn flowOf(ServerResponseResult(true, null, userRoute))
        }
        val bundle = RouteEditFragmentArgs(routeType, null, routeName).toBundle()
        launchFragmentInHiltContainer<RouteEditFragment>(bundle)

        onView(withId(R.id.routeEditRouteNameEditText))
            .check(matches(isDisplayed()))
            .check(matches(withHint((R.string.route_name_hint))))
            .check(matches(withText(routeName)))
        onView(withId(R.id.routeEditHikeDescriptionEditText))
            .check(matches(isDisplayed()))
            .check(matches(withHint(R.string.hike_description_hint)))
            .check(matches(withText("")))
        onView(withId(R.id.routeEditEditButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.edit_route_edit_finalize_button_text)))
        onView(withId(R.id.routeEditDeleteSwitch))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.delete_text)))
        onView(withId(R.id.routeEditSpinner))
            .check(matches(isDisplayed()))
        onView(withId(R.id.routeEditMap))
            .check(matches(isDisplayed()))
    }
}