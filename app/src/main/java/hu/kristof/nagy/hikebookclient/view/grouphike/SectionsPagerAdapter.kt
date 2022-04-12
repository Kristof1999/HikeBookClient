package hu.kristof.nagy.hikebookclient.view.grouphike

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.kristof.nagy.hikebookclient.R

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> GroupHikeListFragment.newInstance(false)
            1 -> GroupHikeListFragment.newInstance(true)
            else -> throw IllegalArgumentException("Unexpected position: $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    companion object {
        private val TAB_TITLES = arrayOf(
            R.string.tab_unconnected_text,
            R.string.tab_connected_text
        )
    }
}