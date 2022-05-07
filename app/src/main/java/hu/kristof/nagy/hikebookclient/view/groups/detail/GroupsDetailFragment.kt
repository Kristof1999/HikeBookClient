package hu.kristof.nagy.hikebookclient.view.groups.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailBinding
import hu.kristof.nagy.hikebookclient.util.showGenericErrorOr
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailViewModel

/**
 * A Fragment that displays the details of the group.
 * It displays the name of the group.
 * It has a button, with which the user can join or leave the group.
 */
@AndroidEntryPoint
class GroupsDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel: GroupsDetailViewModel by viewModels()
        val args: GroupsDetailFragmentArgs by navArgs()

        val binding = FragmentGroupsDetailBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                context = requireContext()
                this.viewModel = viewModel
                groupName = args.groupName
                isConnectedPage = args.isConnectedPage
                executePendingBindings()
            }

        setupObserver(viewModel, args.isConnectedPage)

        val adapter = SectionsPagerAdapter(
            requireContext(),
            childFragmentManager,
            args.groupName,
            args.isConnectedPage
        )
        val viewPager = binding.groupsDetailViewPager
        viewPager.adapter = adapter
        val bottomNav = binding.groupsDetailBottomNav
        bottomNav.setOnItemSelectedListener { menuItem ->
            setupBottomNav(menuItem, viewPager)
        }

        return binding.root
    }

    private fun setupObserver(
        viewModel: GroupsDetailViewModel,
        isConnectedPage: Boolean
    ) {
        viewModel.generalConnectRes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { generalConnectRes ->
                showGenericErrorOr(context, generalConnectRes) {
                    if (isConnectedPage) {
                        Toast.makeText(requireContext(), "A lecsatlakozás sikeres!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "A csatlakozás sikeres!", Toast.LENGTH_LONG).show()
                    }
                    findNavController(requireActivity(), R.id.navHostFragment).navigate(
                        R.id.action_groupsDetailFragment_to_groupsFragment
                    )
                }
            }
        }
    }

    private fun setupBottomNav(
        menuItem: MenuItem,
        viewPager: ViewPager
    ): Boolean {
        when (menuItem.itemId) {
            R.id.groupsDetailMapMenuItem -> {
                viewPager.currentItem = 0
            }
            R.id.groupsDetailListMenuItem -> {
                viewPager.currentItem = 1
            }
            R.id.groupsDetailMembersMenuItem -> {
                viewPager.currentItem = 2
            }
        }
        return true
    }
}