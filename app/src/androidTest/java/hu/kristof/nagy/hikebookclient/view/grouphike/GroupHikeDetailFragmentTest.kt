package hu.kristof.nagy.hikebookclient.view.grouphike

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
import hu.kristof.nagy.hikebookclient.data.repository.IGroupHikeRepository
import hu.kristof.nagy.hikebookclient.di.GroupHikeRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.model.DateTime
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

@UninstallModules(GroupHikeRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class GroupHikeDetailFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @BindValue
    lateinit var groupHikeRepository: IGroupHikeRepository

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    @Test
    fun checkDisplay() {
        val groupHikeName = "groupHike"
        val description = "description"
        val user1 = "user1"
        val user2 = "user2"
        val groupHikeRoute = Route.GroupHikeRoute(
            groupHikeName, "route", listOf(
                Point(0.0, 0.0, MarkerType.SET, ""),
                Point(0.0, 0.0, MarkerType.NEW, "")
            ), description
        )
        groupHikeRepository = mock {
            onBlocking {
                loadRoute(groupHikeName)
            } doReturn ServerResponseResult(true, null, groupHikeRoute)

            onBlocking {
                listParticipants(groupHikeName)
            } doReturn ServerResponseResult(true, null, listOf(user1, user2))
        }
        val dateTime = DateTime(2022, 5, 1, 12, 0)
        val bundle = GroupHikeDetailFragmentArgs(groupHikeName, false, dateTime).toBundle()
        launchFragmentInHiltContainer<GroupHikeDetailFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.groupHikeDetailDescriptionTv))
            .check(matches(isDisplayed()))
            .check(matches(withText(description)))
        onView(withId(R.id.groupHikeDetailNameTv))
            .check(matches(isDisplayed()))
            .check(matches(withText(groupHikeName)))
        onView(withId(R.id.groupHikeDetailMap)).check(matches(isDisplayed()))
        onView(withId(R.id.groupHikeDetailRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withChild(withText(user1)))))
            .check(matches(hasDescendant(withChild(withText(user2)))))
        onView(withId(R.id.groupHikeDetailAddToMyMapButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText(R.string.add_to_my_map_text)))
        onView(withId(R.id.groupHikeDetailGeneralConnectButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(withText("Csatlakozás")))
    }
}