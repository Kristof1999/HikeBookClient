package hu.kristof.nagy.hikebookclient.view.groups.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailListBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.Constants
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGroupsDetailListBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        val mapViewModel: GroupsDetailMapViewModel by activityViewModels()
        val listViewModel: GroupsDetailListViewModel by viewModels()
        val groupName = requireArguments().getString(Constants.GROUP_NAME_BUNDLE_KEY)!!
        val isConnectedPage = requireArguments().getBoolean(Constants.IS_CONNECTED_PAGE_BUNDLE_KEY)!!

        setupObservers(mapViewModel, listViewModel, groupName)

        val adapter = initAdapter(mapViewModel, listViewModel, groupName, isConnectedPage)
        binding.groupsDetailListRecyclerView.adapter = adapter
        mapViewModel.routes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { routes ->
                adapter.submitList(routes.map { it.routeName }.toMutableList())
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObservers(
        mapViewModel: GroupsDetailMapViewModel,
        listViewModel: GroupsDetailListViewModel,
        groupName: String
    ) {
        with(listViewModel) {
            deleteRes.observe(viewLifecycleOwner) { res ->
                if (!listViewModel.deleteFinished) {
                    handleResult(context, res) { deleteRes ->
                        showGenericErrorOr(context, deleteRes) {
                            Toast.makeText(context, "A törlés sikeres!", Toast.LENGTH_SHORT).show()
                            handleOfflineLoad(requireContext()) {
                                mapViewModel.loadRoutesOfGroup(groupName) // refresh
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

    private fun initAdapter(
        mapViewModel: GroupsDetailMapViewModel,
        listViewModel: GroupsDetailListViewModel,
        groupName: String,
        isConnectedPage: Boolean
    ) = GroupsDetailListAdapter(GroupsDetailListClickListener(
        editListener = { routeName ->
            val directions = GroupsDetailFragmentDirections
                .actionGroupsDetailFragmentToRouteEditFragment(RouteType.GROUP, groupName, routeName)
            findNavController().navigate(directions)
        },
        deleteListener = { routeName ->
            handleOffline(requireContext()) {
                listViewModel.delete(groupName, routeName)
            }
        },
        addToMyMapListener = { routeName ->
            handleOffline(requireContext()) {
                val route = mapViewModel.getRoute(routeName)
                listViewModel.addToMyMap(route)
            }
        }
    ), isConnectedPage)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.GROUPS_DETAIL_LIST
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance(groupName: String, isConnectedPage: Boolean): GroupsDetailListFragment {
            return GroupsDetailListFragment().apply {
                arguments = bundleOf(
                    Constants.GROUP_NAME_BUNDLE_KEY to groupName,
                    Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage
                )
            }
        }
    }
}