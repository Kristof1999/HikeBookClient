package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsListBinding
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.util.showGenericErrorOr
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsListViewModel

/**
 * A Fragment which displays the list of groups.
 * A list item consists of a group' name, and a button.
 * With this button, the user can join or leave the group.
 */
@AndroidEntryPoint
class GroupsListFragment : Fragment() {
    private lateinit var binding: FragmentGroupsListBinding
    private val viewModel: GroupsListViewModel by viewModels()

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

        val isConnectedPage = arguments?.getBoolean(IS_CONNECTED_PAGE_BUNDLE_KEY)!!

        setupList(isConnectedPage)

        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.generalConnectRes.observe(viewLifecycleOwner) { res ->
            onGeneralConnectRes(res, isConnectedPage)
        }
    }

    private fun onGeneralConnectRes(res: Boolean, isConnectedPage: Boolean) {
        if (!viewModel.generalConnectFinished) {
            showGenericErrorOr(context, res) {
                if (isConnectedPage) {
                    Toast.makeText(requireContext(), "A lecsatlakozás sikeres!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "A csatlakozás sikeres!", Toast.LENGTH_LONG).show()
                }
            }
            viewModel.generalConnectFinished = true
        }
    }

    private fun setupList(isConnectedPage: Boolean) {
        val adapter = initAdapter(isConnectedPage)
        binding.groupsRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.groups.observe(viewLifecycleOwner) { groupNames ->
            adapter.submitList(groupNames.toMutableList())
        }
    }

    private fun initAdapter(isConnectedPage: Boolean) = GroupsListAdapter(
        isConnectedPage, GroupsClickListener(
            connectListener = { groupName, isConnectedPage ->
                handleOffline(requireContext()) {
                    viewModel.generalConnect(groupName, isConnectedPage)
                }
            },
            detailListener = { groupName, isConnectedPage ->
                val directions = GroupsFragmentDirections
                    .actionGroupsFragmentToGroupsDetailFragment(groupName, isConnectedPage)
                findNavController().navigate(directions)
            }
        )
    )

    override fun onResume() {
        super.onResume()
        val isConnectedPage = arguments?.getBoolean(IS_CONNECTED_PAGE_BUNDLE_KEY)!!
        handleOfflineLoad(requireContext()) {
            viewModel.listGroups(isConnectedPage)
        }
    }

    companion object {
        private const val IS_CONNECTED_PAGE_BUNDLE_KEY =  "isConnectedPage"

        fun newInstance(isConnectedPage: Boolean): GroupsListFragment {
            return GroupsListFragment().apply {
                arguments = bundleOf(IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage)
            }
        }
    }
}