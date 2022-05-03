package hu.kristof.nagy.hikebookclient.view.groups.detail

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.kristof.nagy.hikebookclient.R

class SectionsPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    private val groupName: String,
    private val isConnectedPage: Boolean
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> GroupsDetailMapFragment.newInstance(groupName, isConnectedPage)
            1 -> GroupsDetailListFragment.newInstance(groupName, isConnectedPage)
            2 -> GroupsDetailMembersFragment.newInstance(groupName)
            else -> throw IllegalArgumentException("Unexpected position: $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    companion object {
        private val TAB_TITLES = arrayOf(
            R.string.groups_map_menu_text,
            R.string.groups_list_menu_text,
            R.string.groups_members_menu_text
        )
    }
}