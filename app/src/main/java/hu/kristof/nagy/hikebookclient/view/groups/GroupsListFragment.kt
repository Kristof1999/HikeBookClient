package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsListBinding
import hu.kristof.nagy.hikebookclient.model.ResponseResult
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
        binding = FragmentGroupsListBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        setupObserver()

        setupList()

        return binding.root
    }

    private fun setupObserver() {
        val isConnectedPage = arguments?.getBoolean(IS_CONNECTED_PAGE_BUNDLE_KEY)!!
        viewModel.generalConnectRes.observe(viewLifecycleOwner) { res ->
            onGeneralConnectRes(res, isConnectedPage)
        }
    }

    private fun onGeneralConnectRes(res: ResponseResult<Boolean>, isConnectedPage: Boolean) {
        if (!viewModel.generalConnectFinished) {
            handleResult(context, res) { generalConnectRes ->
                showGenericErrorOr(context, generalConnectRes) {
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
    }

    private fun setupList() {
        val isConnectedPage = arguments?.getBoolean(IS_CONNECTED_PAGE_BUNDLE_KEY)!!
        val adapter = initAdapter(isConnectedPage)
        binding.groupsRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.groups.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { groupNames ->
                adapter.submitList(groupNames.toMutableList())
            }
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