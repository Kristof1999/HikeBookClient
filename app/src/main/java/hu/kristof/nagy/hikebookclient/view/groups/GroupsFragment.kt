package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsBinding

class GroupsFragment : Fragment() {
    private lateinit var binding: FragmentGroupsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_groups, container, false
        )

        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), parentFragmentManager)
        val viewPager: ViewPager = binding.groupsViewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.groupsTabLayout
        tabs.setupWithViewPager(viewPager)

        return binding.root
    }
}