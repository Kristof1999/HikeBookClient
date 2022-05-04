package hu.kristof.nagy.hikebookclient.view.groups

import androidx.core.os.bundleOf
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
import hu.kristof.nagy.hikebookclient.data.repository.IGroupsRepository
import hu.kristof.nagy.hikebookclient.di.GroupsRepositoryModule
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResource
import hu.kristof.nagy.hikebookclient.util.DataBindingIdlingResourceRule
import hu.kristof.nagy.hikebookclient.view.groups.detail.GroupsDetailMembersFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@UninstallModules(GroupsRepositoryModule::class)
@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class GroupsDetailMembersFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(dataBindingIdlingResource)

    @Mock
    @BindValue
    lateinit var groupsRepository: IGroupsRepository

    @Test
    fun checkList() {
        val groupName = "group"
        val user1 = "user1"
        val user2 = "user2"
        groupsRepository = mock {
            onBlocking {
                listMembers(groupName)
            } doReturn ServerResponseResult(true, null, listOf(
                user1, user2
            ))
        }
        val bundle = bundleOf(
            Constants.GROUP_NAME_BUNDLE_KEY to groupName
        )
        launchFragmentInHiltContainer<GroupsDetailMembersFragment>(bundle, dataBindingIdlingResource)

        onView(withId(R.id.groupsDetailMembersRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withChild(withId(R.id.groupsDetailMemberNameTv)))))
            .check(matches(hasDescendant(withChild(withText(user1)))))
            .check(matches(hasDescendant(withChild(withText(user2)))))
    }
}