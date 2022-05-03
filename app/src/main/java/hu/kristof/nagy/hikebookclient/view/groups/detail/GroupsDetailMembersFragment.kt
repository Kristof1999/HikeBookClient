package hu.kristof.nagy.hikebookclient.view.groups.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailMembersBinding
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMembersViewModel

/**
 * A Fragment which displays the list of members of the given group.
 * A list item consists of a member's name.
 */
@AndroidEntryPoint
class GroupsDetailMembersFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGroupsDetailMembersBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        val viewModel: GroupsDetailMembersViewModel by viewModels()

        setupList(viewModel, binding)
        handleOfflineLoad(requireContext()) {
            val groupName = requireArguments().getString(Constants.GROUP_NAME_BUNDLE_KEY)!!
            viewModel.listMembers(groupName)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupList(
        viewModel: GroupsDetailMembersViewModel,
        binding: FragmentGroupsDetailMembersBinding
    ) {
        val adapter = GroupsDetailMembersAdapter()
        viewModel.members.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { members ->
                adapter.submitList(members.toMutableList())
            }
        }
        binding.groupsDetailMembersRecyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.GROUPS_DETAIL_MEMBERS
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance(groupName: String): GroupsDetailMembersFragment {
            return GroupsDetailMembersFragment().apply {
                arguments = bundleOf(Constants.GROUP_NAME_BUNDLE_KEY to groupName)
            }
        }
    }
}