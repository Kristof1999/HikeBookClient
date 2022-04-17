package hu.kristof.nagy.hikebookclient.view.grouphike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupHikeBinding

/**
 * A Fragment which displays 2 tabs:
 * a list of group hikes which the user has not joined,
 * and a list which the user has joined
 */
class GroupHikeFragment : Fragment() {
    private lateinit var binding: FragmentGroupHikeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_group_hike, container, false
        )

        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), childFragmentManager)
        val viewPager: ViewPager = binding.groupHikeViewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.groupHikeTabLayout
        tabs.setupWithViewPager(viewPager)

        return binding.root
    }
}