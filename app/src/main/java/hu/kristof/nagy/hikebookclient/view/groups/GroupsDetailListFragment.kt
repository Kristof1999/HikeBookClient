package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailListBinding
import hu.kristof.nagy.hikebookclient.util.throwGenericErrorOr
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

        val args: GroupsDetailListFragmentArgs by navArgs()
        val viewModel: GroupsDetailMapViewModel by activityViewModels()

        val adapter = initAdapter(viewModel, args)
        binding.groupsDetailListRecyclerView.adapter = adapter

        observeViewModel(viewModel, adapter, args)
    }

    private fun observeViewModel(
        viewModel: GroupsDetailMapViewModel,
        adapter: GroupsDetailListAdapter,
        args: GroupsDetailListFragmentArgs
    ) {
        binding.lifecycleOwner = viewLifecycleOwner
        with(viewModel) {
            routes.observe(viewLifecycleOwner) { res ->
                handleResult(context, res) { routes ->
                    adapter.submitList(routes.map { it.routeName }.toMutableList())
                }
            }
            deleteRes.observe(viewLifecycleOwner) { res ->
                handleResult(context, res) { deleteRes ->
                    throwGenericErrorOr(context, deleteRes) {
                        Toast.makeText(context, "A törlés sikeres!", Toast.LENGTH_SHORT).show()
                        viewModel.loadRoutesOfGroup(args.groupName) // refresh
                    }
                }
            }
            addToMyMapRes.observe(viewLifecycleOwner) { res ->
                handleResult(context, res) { addToMyMapRes ->
                    throwGenericErrorOr(context, addToMyMapRes, "A hozzáadás sikeres!")
                }
            }
        }
    }

    private fun initAdapter(
        viewModel: GroupsDetailMapViewModel,
        args: GroupsDetailListFragmentArgs
    ) = GroupsDetailListAdapter(GroupsDetailListClickListener(
        editListener = { routeName ->
            val route = viewModel.getRoute(routeName)
            val directions = GroupsDetailFragmentDirections
                .actionGroupsDetailFragmentToRouteEditFragment(route)
            findNavController(requireActivity(), R.id.navHostFragment).navigate(directions)
        },
        deleteListener = { routeName ->
            viewModel.onDelete(args.groupName, routeName)
        },
        addToMyMapListener = { routeName ->
            viewModel.onAddToMyMap(routeName)
        }
    ), args.isConnectedPage)
}