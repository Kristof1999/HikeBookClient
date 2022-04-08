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
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsListViewModel

@AndroidEntryPoint
class GroupsListFragment : Fragment() {
    private lateinit var binding: FragmentGroupsListBinding
    private var isConnectedPage: Boolean? = null
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

        isConnectedPage = arguments?.getBoolean("isConnectedPage")

        val adapter = GroupsListAdapter(isConnectedPage!!, GroupsClickListener(
            connectListener = { groupName, isConnectedPage ->
                viewModel.generalConnect(groupName, isConnectedPage)
            },
            detailListener = { groupName, isConnectedPage ->
                val directions = GroupsFragmentDirections
                    .actionGroupsFragmentToGroupsDetailFragment(groupName, isConnectedPage)
                findNavController().navigate(directions)
            })
        )
        binding.groupsRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.groups.observe(viewLifecycleOwner) { groupNames ->
            adapter.submitList(groupNames.toMutableList())
        }
        viewModel.generalConnectRes.observe(viewLifecycleOwner) { res ->
            if (res) {
                if (isConnectedPage!!) {
                    Toast.makeText(requireContext(), "A lecsatlakozás sikeres!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "A csatlakozás sikeres!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Valami hiba történt.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isConnectedPage = arguments?.getBoolean("isConnectedPage")
        viewModel.listGroups(isConnectedPage!!)
    }

    companion object {
        fun newInstance(isConnectedPage: Boolean): GroupsListFragment {
            return GroupsListFragment().apply {
                arguments = bundleOf("isConnectedPage" to isConnectedPage)
            }
        }
    }
}