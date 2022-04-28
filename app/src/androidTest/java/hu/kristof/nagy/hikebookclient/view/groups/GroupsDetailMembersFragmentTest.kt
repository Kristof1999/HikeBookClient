package hu.kristof.nagy.hikebookclient.view.groups

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import hu.kristof.nagy.hikebookclient.launchFragmentInHiltContainer
import org.junit.Rule
import org.junit.runner.RunWith

@HiltAndroidTest
@MediumTest
@RunWith(AndroidJUnit4::class)
class GroupsDetailMembersFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    fun checkList() {
        launchFragmentInHiltContainer<GroupsDetailMembersFragment>()

    }
}