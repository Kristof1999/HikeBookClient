package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import hu.kristof.nagy.hikebookclient.GroupsNavigationDirections
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsDetailBinding

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
        binding.groupsDetailGroupNameTv.text = args.groupName
        if (args.isConnectedPage) {
            binding.groupsDetailConnectButton.text = "Elhagyás"
        } else {
            binding.groupsDetailConnectButton.text = "Csatlakozás"
        }

        val navController = findNavController(requireActivity(), R.id.groupsDetailNavHostFragment)

        // set start destination's arguments
        val bundle = bundleOf("groupName" to args.groupName)
        navController.setGraph(R.navigation.groups_navigation, bundle)

        val bottomNav = binding.groupsDetailBottomNav
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.groupsDetailMapMenuItem -> {
                    navController.navigate(
                        R.id.action_global_groupsDetailMapFragment
                    )
                }
                R.id.groupsDetailMembersMenuItem -> {
                    val directions = GroupsNavigationDirections
                        .actionGlobalGroupsDetailMembersFragment(args.groupName)
                    navController.navigate(directions)
                }
            }
            return@setOnItemSelectedListener true
        }
    }
}