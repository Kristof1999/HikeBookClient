package hu.kristof.nagy.hikebookclient.view.browse

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
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
import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@UninstallModules(UserRouteRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class BrowseListFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    lateinit var userRouteRepository: IUserRouteRepository

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    @Test
    fun checkDisplay() {
        val userName = "user"
        val routeName = "route"
        userRouteRepository = mock {
            onBlocking {
                listUserRoutesForLoggedInUser()
            } doReturn flowOf(
                ServerResponseResult(true, null, listOf(
                    BrowseListItem(userName, routeName)
                ))
            )
        }
        launchFragmentInHiltContainer<BrowseListFragment>(
            dataBindingIdlingResource = dataBindingIdlingResource
        )

        onView(withId(R.id.browseRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withChild(withId(R.id.browseListItemUserName)))))
            .check(matches(hasDescendant(withChild(withText(userName)))))
            .check(matches(hasDescendant(withChild(withId(R.id.browseListItemRouteName)))))
            .check(matches(hasDescendant(withChild(withText(routeName)))))
    }

    @Test
    fun verifyNav() {
        val userName = "user"
        val routeName = "route"
        userRouteRepository = mock {
            onBlocking {
                listUserRoutesForLoggedInUser()
            } doReturn flowOf(
                ServerResponseResult(true, null, listOf(
                    BrowseListItem(userName, routeName)
                ))
            )
        }
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<BrowseListFragment>(
            dataBindingIdlingResource = dataBindingIdlingResource
        ) {
            Navigation.setViewNavController(this.view!!, navController)
        }

        onView(withId(R.id.browseRecyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<BrowseListAdapter.ViewHolder>(0, click())
        )

        val directions = BrowseListFragmentDirections
            .actionBrowseListFragmentToBrowseDetailFragment(userName, routeName)
        verify(navController).navigate(directions)
    }
}