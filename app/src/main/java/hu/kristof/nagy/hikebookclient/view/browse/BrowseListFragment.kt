package hu.kristof.nagy.hikebookclient.view.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentBrowseListBinding
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.browse.BrowseViewModel

/**
 * A Fragment to display a list of routes for browsing.
 */
@AndroidEntryPoint
class BrowseListFragment : Fragment() {
    private lateinit var binding: FragmentBrowseListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_browse_list, container, false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: BrowseViewModel by viewModels()

        handleOfflineLoad(requireContext()) {
            viewModel.listRoutes()
        }

        setupRecyclerView(viewModel)
    }

    private fun setupRecyclerView(viewModel: BrowseViewModel) {
        val adapter = BrowseListAdapter(BrowseClickListener { userName, routeName ->
            val action = BrowseListFragmentDirections
                .actionBrowseListFragmentToBrowseDetailFragment(userName, routeName)
            findNavController().navigate(action)
        })
        binding.browseRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.BROWSE_LIST
            val action = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(action)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}