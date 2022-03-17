package hu.kristof.nagy.hikebookclient.view.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentBrowseListBinding
import dagger.hilt.android.AndroidEntryPoint
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
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_browse_list, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: BrowseViewModel by viewModels()
        viewModel.listRoutes()
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
}