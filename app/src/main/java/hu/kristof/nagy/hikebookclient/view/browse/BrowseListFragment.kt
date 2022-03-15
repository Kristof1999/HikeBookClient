package hu.kristof.nagy.hikebookclient.view.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentBrowseListBinding
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.viewModel.browse.BrowseViewModel

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
        viewModel.onLoadRoutes()
        val adapter = BrowseListAdapter()
        binding.browseRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}