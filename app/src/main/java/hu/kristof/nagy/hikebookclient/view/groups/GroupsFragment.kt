package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsBinding
import hu.kristof.nagy.hikebookclient.util.catchAndShowIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.showGenericErrorOr
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.view.routes.TextDialogFragment
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsViewModel

/**
 * A Fragment that has 2 tabs:
 * one to display a list of groups which the user has not joined,
 * and one which the user has joined.
 * It has a button with which the user can create a group.
 */
@AndroidEntryPoint
class GroupsFragment : Fragment() {
    private lateinit var binding: FragmentGroupsBinding
    private val viewModel: GroupsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentGroupsBinding>(
            inflater, R.layout.fragment_groups, container, false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        setupObserver()

        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), childFragmentManager)
        val viewPager: ViewPager = binding.groupsViewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.groupsTabLayout
        tabs.setupWithViewPager(viewPager)

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setupObserver() {
        viewModel.createRes.observe(viewLifecycleOwner) { res ->
            if (!viewModel.createFinished) {
                handleResult(requireContext(), res) { createRes ->
                    showGenericErrorOr(context, createRes, "Csoport sikeresen létrehozva!")
                }
                viewModel.createFinished = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGroupCreation(viewModel)
    }

    private fun setupGroupCreation(viewModel: GroupsViewModel) {
        val dialogFragment = TextDialogFragment.instanceOf(
            R.string.groups_create_dialog_text, R.string.groups_create_dialog_hint_text
        )
        dialogFragment.text.observe(viewLifecycleOwner) { name ->
            catchAndShowIllegalStateAndArgument(requireContext()) {
                viewModel.createGroup(name)
            }
        }
        binding.groupsGroupCreateButton.setOnClickListener {
            handleOffline(requireContext()) {
                dialogFragment.show(parentFragmentManager, "group create")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.GROUPS
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}