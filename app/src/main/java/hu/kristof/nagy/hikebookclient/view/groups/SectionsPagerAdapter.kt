package hu.kristof.nagy.hikebookclient.view.groups

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.kristof.nagy.hikebookclient.R

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> GroupsListFragment.newInstance(false)
            1 -> GroupsListFragment.newInstance(true)
            else -> throw IllegalArgumentException("Unexpected position: $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int = 2

    companion object {
        private val TAB_TITLES = arrayOf(
            R.string.tab_groups_unconnected_text,
            R.string.tab_groups_connected_text
        )
    }
}