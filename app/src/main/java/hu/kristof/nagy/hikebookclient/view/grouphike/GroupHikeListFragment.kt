package hu.kristof.nagy.hikebookclient.view.grouphike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupHikeListBinding

class GroupHikeListFragment : Fragment() {
    private lateinit var binding: FragmentGroupHikeListBinding
    private var isConnectedPage: Boolean? = null

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

        isConnectedPage = arguments?.getBoolean(IS_CONNECTED_PAGE_BUNDLE_KEY)

        val adapter = GroupHikeListAdapter()
        binding.groupHikeListRecyclerView.adapter = adapter
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