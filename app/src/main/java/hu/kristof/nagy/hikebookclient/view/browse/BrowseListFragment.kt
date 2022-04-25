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
    private lateinit var binding: FragmentBrowseListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBrowseListBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: BrowseViewModel by viewModels()

        setupList(viewModel)
        handleOfflineLoad(requireContext()) {
            viewModel.listRoutes()
        }
    }

    private fun setupList(viewModel: BrowseViewModel) {
        val adapter = BrowseListAdapter(BrowseClickListener { userName, routeName ->
            val action = BrowseListFragmentDirections
                .actionBrowseListFragmentToBrowseDetailFragment(userName, routeName)
            findNavController().navigate(action)
        })
        binding.browseRecyclerView.adapter = adapter
        viewModel.routes.observe(viewLifecycleOwner) { res ->
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