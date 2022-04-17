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
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.util.showGenericErrorOr
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMapViewModel

/**
 * A Fragment to display the routes of a group in a list.
 * A list item consists of the route's name, and 3 buttons:
 * one to edit and one to delete the given route, and
 * one to add the route to the user's map.
 */
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
                if (!viewModel.deleteFinished) {
                    handleResult(context, res) { deleteRes ->
                        showGenericErrorOr(context, deleteRes) {
                            Toast.makeText(context, "A törlés sikeres!", Toast.LENGTH_SHORT).show()
                            handleOfflineLoad(requireContext()) {
                                viewModel.loadRoutesOfGroup(args.groupName) // refresh
                            }
                        }
                    }
                    viewModel.deleteFinished = true
                }
            }
            addToMyMapRes.observe(viewLifecycleOwner) { res ->
                if (!viewModel.addToMyMapFinished) {
                    handleResult(context, res) { addToMyMapRes ->
                        showGenericErrorOr(context, addToMyMapRes, "A hozzáadás sikeres!")
                    }
                    viewModel.addToMyMapFinished = true
                }
            }
        }
    }

    private fun initAdapter(
        viewModel: GroupsDetailMapViewModel,
        args: GroupsDetailListFragmentArgs
    ) = GroupsDetailListAdapter(GroupsDetailListClickListener(
        editListener = { routeName ->
            val directions = GroupsDetailFragmentDirections
                .actionGroupsDetailFragmentToRouteEditFragment(RouteType.GROUP, args.groupName, routeName)
            findNavController(requireActivity(), R.id.navHostFragment).navigate(directions)
        },
        deleteListener = { routeName ->
            handleOffline(requireContext()) {
                viewModel.onDelete(args.groupName, routeName)
            }
        },
        addToMyMapListener = { routeName ->
            handleOffline(requireContext()) {
                viewModel.onAddToMyMap(routeName)
            }
        }
    ), args.isConnectedPage)
}