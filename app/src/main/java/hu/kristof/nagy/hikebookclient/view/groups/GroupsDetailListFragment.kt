package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailListBinding
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMapViewModel

class GroupsDetailListFragment : Fragment() {
    private lateinit var binding: FragmentGroupsDetailListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_groups_detail_list, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: GroupsDetailMapViewModel by activityViewModels()
        val adapter = GroupsDetailListAdapter()
        binding.groupsDetailListRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { routes ->
                adapter.submitList(routes.map { it.routeName }.toMutableList())
            }
        }
    }
}