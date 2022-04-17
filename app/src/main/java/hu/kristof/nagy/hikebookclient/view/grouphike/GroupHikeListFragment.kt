package hu.kristof.nagy.hikebookclient.view.grouphike

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
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupHikeListBinding
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.util.showGenericErrorOr
import hu.kristof.nagy.hikebookclient.viewModel.grouphike.GroupHikeListViewModel

/**
 * A Fragment to display a list of group hikes.
 * A list item consists of
 * the group hike's name, date, and
 * it also has a button with which the user
 * can join or leave the given group hike.
 */
@AndroidEntryPoint
class GroupHikeListFragment : Fragment() {
    private lateinit var binding: FragmentGroupHikeListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_group_hike_list, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isConnectedPage = arguments?.getBoolean(IS_CONNECTED_PAGE_BUNDLE_KEY)!!

        val viewModel: GroupHikeListViewModel by viewModels()

        setupLoad(isConnectedPage, viewModel)

        handleGeneralConnect(viewModel, isConnectedPage)
    }

    private fun handleGeneralConnect(
        viewModel: GroupHikeListViewModel,
        isConnectedPage: Boolean
    ) {
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.generalConnectRes.observe(viewLifecycleOwner) { generalConnectRes ->
            if (!viewModel.generalConnectFinished) {
                showGenericErrorOr(context, generalConnectRes) {
                    if (isConnectedPage) {
                        Toast.makeText(context, "A lecsatlakozás sikeres!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "A csatlakozás sikeres!", Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.generalConnectFinished = true
            }
        }
    }

    private fun setupLoad(
        isConnectedPage: Boolean,
        viewModel: GroupHikeListViewModel
    ) {
        setupList(isConnectedPage, viewModel)
        handleOfflineLoad(requireContext()) {
            viewModel.listGroupHikes(isConnectedPage)
        }
    }

    private fun setupList(
        isConnectedPage: Boolean,
        viewModel: GroupHikeListViewModel
    ) {
        val adapter = GroupHikeListAdapter(isConnectedPage,
            GroupHikeClickListener(
                generalConnectListener = { groupHikeName, dateTime ->
                    handleOffline(requireContext()) {
                        viewModel.generalConnect(groupHikeName, isConnectedPage, dateTime)
                    }
                },
                detailNavListener = { groupHikeName, dateTime ->
                    val directions = GroupHikeFragmentDirections
                        .actionGroupHikeFragmentToGroupHikeDetailFragment(
                            groupHikeName,
                            isConnectedPage,
                            dateTime
                        )
                    findNavController().navigate(directions)
                }
            ))
        binding.groupHikeListRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.groupHikes.observe(viewLifecycleOwner) { groupHikes ->
            adapter.submitList(groupHikes.toMutableList())
        }
    }

    companion object {
        private const val IS_CONNECTED_PAGE_BUNDLE_KEY =  "isConnectedPage"

        fun newInstance(isConnectedPage: Boolean): GroupHikeListFragment {
            return GroupHikeListFragment().apply {
                arguments = bundleOf(IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage)
            }
        }
    }
}