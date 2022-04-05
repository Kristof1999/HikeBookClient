package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsListBinding
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsListViewModel

@AndroidEntryPoint
class GroupsListFragment : Fragment() {
    private var isConnectedPage: Boolean? = null
    private lateinit var binding: FragmentGroupsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_groups_list, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: GroupsListViewModel by viewModels()
        viewModel.listGroups(isConnectedPage!!)

        val adapter = GroupsListAdapter(isConnectedPage!!, GroupsClickListener(
            connectListener = { groupName, isConnectedPage ->
                // viewmodel call
            },
            detailListener = {groupName, isConnectedPage ->
                val directions = GroupsFragmentDirections
                    .actionGroupsFragmentToGroupsDetailFragment(groupName, isConnectedPage)
                findNavController().navigate(directions)
            })
        )
        binding.groupsRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.groups.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }
    }

    companion object {
        fun newInstance(isConnectedPage: Boolean): GroupsListFragment {
            return GroupsListFragment().apply {
                this.isConnectedPage = isConnectedPage
            }
        }
    }
}