package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailMembersBinding
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMembersViewModel

/**
 * A Fragment which displays the list of members of the given group.
 * A list item consists of a member's name.
 */
@AndroidEntryPoint
class GroupsDetailMembersFragment : Fragment() {
    private lateinit var binding: FragmentGroupsDetailMembersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_groups_detail_members, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: GroupsDetailMembersViewModel by viewModels()
        val args: GroupsDetailMembersFragmentArgs by navArgs()

        setupList(viewModel)
        handleOfflineLoad(requireContext()) {
            viewModel.listMembers(args.groupName)
        }
    }

    private fun setupList(viewModel: GroupsDetailMembersViewModel) {
        val adapter = GroupsDetailMembersAdapter()
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.members.observe(viewLifecycleOwner) { members ->
            adapter.submitList(members.toMutableList())
        }
        binding.groupsDetailMembersRecyclerView.adapter = adapter
    }
}