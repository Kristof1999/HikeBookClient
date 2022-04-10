package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.GroupsNavigationDirections
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailBinding
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.viewModel.groups.GroupsDetailViewModel

@AndroidEntryPoint
class GroupsDetailFragment : Fragment() {
    private lateinit var binding: FragmentGroupsDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_groups_detail, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: GroupsDetailFragmentArgs by navArgs()
        adaptView(args)

        val viewModel: GroupsDetailViewModel by viewModels()
        binding.groupsDetailConnectButton.setOnClickListener {
            viewModel.generalConnect(args.groupName, args.isConnectedPage)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.generalConnectRes.observe(viewLifecycleOwner) { generalConnectRes ->
            if (generalConnectRes) {
                findNavController(requireActivity(), R.id.navHostFragment).navigate(
                    R.id.action_groupsDetailFragment_to_groupsFragment
                )
            } else {
                Toast.makeText(requireContext(), "Valamilyen hiba történt.", Toast.LENGTH_SHORT).show()
            }
        }

        val navController = findNavController(requireActivity(), R.id.groupsDetailNavHostFragment)

        // set start destination's arguments
        val bundle = bundleOf(
            Constants.GROUP_NAME_BUNDLE_KEY to args.groupName,
            Constants.IS_CONNECTED_PAGE_BUNDLE_KEY to args.isConnectedPage
        )
        navController.setGraph(R.navigation.groups_navigation, bundle)

        val bottomNav = binding.groupsDetailBottomNav
        bottomNav.setOnItemSelectedListener { menuItem ->
            return@setOnItemSelectedListener setupBottomNav(menuItem, navController, bundle, args)
        }
    }

    private fun setupBottomNav(
        menuItem: MenuItem,
        navController: NavController,
        bundle: Bundle,
        args: GroupsDetailFragmentArgs
    ): Boolean {
        when (menuItem.itemId) {
            R.id.groupsDetailMapMenuItem -> {
                navController.navigate(
                    R.id.action_global_groupsDetailMapFragment, bundle
                )
            }
            R.id.groupsDetailListMenuItem -> {
                val directions = GroupsNavigationDirections
                    .actionGlobalGroupsDetailListFragment(args.groupName, args.isConnectedPage)
                navController.navigate(directions)
            }
            R.id.groupsDetailMembersMenuItem -> {
                val directions = GroupsNavigationDirections
                    .actionGlobalGroupsDetailMembersFragment(args.groupName)
                navController.navigate(directions)
            }
        }
        return true
    }

    private fun adaptView(args: GroupsDetailFragmentArgs) {
        binding.groupsDetailGroupNameTv.text = args.groupName
        if (args.isConnectedPage) {
            binding.groupsDetailConnectButton.text = "Elhagyás"
        } else {
            binding.groupsDetailConnectButton.text = "Csatlakozás"
        }
    }
}