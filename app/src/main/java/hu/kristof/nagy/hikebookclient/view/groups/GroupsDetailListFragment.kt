package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailListBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.util.showGenericErrorOr
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailListViewModel
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailMapViewModel

/**
 * A Fragment to display the routes of a group in a list.
 * A list item consists of the route's name, and 3 buttons:
 * one to edit and one to delete the given route, and
 * one to add the route to the user's map.
 */
@AndroidEntryPoint
class GroupsDetailListFragment : Fragment() {
    private lateinit var binding: FragmentGroupsDetailListBinding
    private val mapViewModel: GroupsDetailMapViewModel by activityViewModels()
    private val listViewModel: GroupsDetailListViewModel by viewModels()
    private val args: GroupsDetailListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupsDetailListBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        setupObservers()

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObservers() {
        with(listViewModel) {
            deleteRes.observe(viewLifecycleOwner) { res ->
                if (!listViewModel.deleteFinished) {
                    handleResult(context, res) { deleteRes ->
                        showGenericErrorOr(context, deleteRes) {
                            Toast.makeText(context, "A törlés sikeres!", Toast.LENGTH_SHORT).show()
                            handleOfflineLoad(requireContext()) {
                                mapViewModel.loadRoutesOfGroup(args.groupName) // refresh
                            }
                        }
                    }
                    listViewModel.deleteFinished = true
                }
            }
            addToMyMapRes.observe(viewLifecycleOwner) { res ->
                if (!listViewModel.addToMyMapFinished) {
                    handleResult(context, res) { addToMyMapRes ->
                        showGenericErrorOr(context, addToMyMapRes, "A hozzáadás sikeres!")
                    }
                    listViewModel.addToMyMapFinished = true
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = initAdapter(mapViewModel, listViewModel, args)
        binding.groupsDetailListRecyclerView.adapter = adapter
        mapViewModel.routes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { routes ->
                adapter.submitList(routes.map { it.routeName }.toMutableList())
            }
        }
    }

    private fun initAdapter(
        mapViewModel: GroupsDetailMapViewModel,
        listViewModel: GroupsDetailListViewModel,
        args: GroupsDetailListFragmentArgs
    ) = GroupsDetailListAdapter(GroupsDetailListClickListener(
        editListener = { routeName ->
            val directions = GroupsDetailFragmentDirections
                .actionGroupsDetailFragmentToRouteEditFragment(RouteType.GROUP, args.groupName, routeName)
            findNavController(requireActivity(), R.id.navHostFragment).navigate(directions)
        },
        deleteListener = { routeName ->
            handleOffline(requireContext()) {
                listViewModel.onDelete(args.groupName, routeName)
            }
        },
        addToMyMapListener = { routeName ->
            handleOffline(requireContext()) {
                val route = mapViewModel.getRoute(routeName)
                listViewModel.onAddToMyMap(route)
            }
        }
    ), args.isConnectedPage)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.GROUPS_DETAIL_LIST
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController(requireActivity(), R.id.navHostFragment).navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}