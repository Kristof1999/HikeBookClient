package hu.kristof.nagy.hikebookclient.view.grouphike

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
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupHikeListBinding
import hu.kristof.nagy.hikebookclient.util.Constants
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
    private val viewModel: GroupHikeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGroupHikeListBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        setupObserver(viewModel)

        val isConnectedPage = arguments?.getBoolean(Constants.IS_CONNECTED_PAGE_BUNDLE_KEY)!!

        setupLoad(isConnectedPage, viewModel, binding)

        return binding.root
    }

    private fun setupObserver(viewModel: GroupHikeListViewModel) {
        viewModel.generalConnectRes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { generalConnectRes ->
                if (!viewModel.generalConnectFinished) {
                    showGenericErrorOr(context, generalConnectRes) {
                        val isConnectedPage = arguments?.getBoolean(Constants.IS_CONNECTED_PAGE_BUNDLE_KEY)!!
                        if (isConnectedPage) {
                            Toast.makeText(context, "A lecsatlakozás sikeres!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "A csatlakozás sikeres!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        handleOfflineLoad(requireContext()) {
                            viewModel.listGroupHikes(isConnectedPage)
                        }
                    }
                    viewModel.generalConnectFinished = true
                }
            }
        }
    }

    private fun setupLoad(
        isConnectedPage: Boolean,
        viewModel: GroupHikeListViewModel,
        binding: FragmentGroupHikeListBinding
    ) {
        setupList(isConnectedPage, viewModel, binding)
        handleOfflineLoad(requireContext()) {
            viewModel.listGroupHikes(isConnectedPage)
        }
    }

    private fun setupList(
        isConnectedPage: Boolean,
        viewModel: GroupHikeListViewModel,
        binding: FragmentGroupHikeListBinding
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
        viewModel.groupHikes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { groupHikes ->
                adapter.submitList(groupHikes.toMutableList())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val isConnectedPage = arguments?.getBoolean(Constants.IS_CONNECTED_PAGE_BUNDLE_KEY)!!
        handleOfflineLoad(requireContext()) {
            viewModel.listGroupHikes(isConnectedPage)
        }
    }

    companion object {
        fun newInstance(isConnectedPage: Boolean): GroupHikeListFragment {
            return GroupHikeListFragment().apply {
                arguments = bundleOf(Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to isConnectedPage)
            }
        }
    }
}