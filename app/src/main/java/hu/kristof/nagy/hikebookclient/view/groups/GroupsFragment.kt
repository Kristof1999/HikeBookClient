package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsBinding
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsViewModel

@AndroidEntryPoint
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: GroupsViewModel by viewModels()
        binding.groupsGroupCreateButton.setOnClickListener {
            val dialogFragment = GroupCreateDialogFragment()
            dialogFragment.show(parentFragmentManager, "group create")

            binding.lifecycleOwner = viewLifecycleOwner
            dialogFragment.name.observe(viewLifecycleOwner) { name ->
                try {
                    viewModel.createGroup(name)
                } catch(e: IllegalArgumentException) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.createRes.observe(viewLifecycleOwner) { res ->
            handleResult(requireContext(), res) { createRes ->
                if (createRes)
                    Toast.makeText(requireContext(), "Csoport sikeresen létrehozva!", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(requireContext(), "Valamilyen hiba történt.", Toast.LENGTH_LONG).show()
            }
        }
    }
}