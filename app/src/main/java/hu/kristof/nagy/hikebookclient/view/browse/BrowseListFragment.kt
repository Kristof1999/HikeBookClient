package hu.kristof.nagy.hikebookclient.view.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentBrowseListBinding
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.browse.BrowseViewModel

/**
 * A Fragment to display a list of routes for browsing.
 * A list item consists of the route's name and the corresponding user's name.
 */
@AndroidEntryPoint
class BrowseListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBrowseListBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        val viewModel: BrowseViewModel by viewModels()

        setupList(viewModel, binding)
        handleOfflineLoad(requireContext()) {
            viewModel.listRoutes()
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupList(
        viewModel: BrowseViewModel,
        binding: FragmentBrowseListBinding
    ) {
        val adapter = BrowseListAdapter(BrowseClickListener { userName, routeName ->
            val directions = BrowseListFragmentDirections
                .actionBrowseListFragmentToBrowseDetailFragment(userName, routeName)
            findNavController().navigate(directions)
        })
        binding.browseRecyclerView.adapter = adapter
        viewModel.items.observe(viewLifecycleOwner) { res ->
            handleResult(requireContext(), res) { list ->
                adapter.submitList(list.toMutableList())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.BROWSE_LIST
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}