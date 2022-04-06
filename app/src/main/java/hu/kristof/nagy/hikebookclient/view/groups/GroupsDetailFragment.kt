package hu.kristof.nagy.hikebookclient.view.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
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

        val navController = findNavController(requireActivity(), R.id.groupsNavHostFragment)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.groupsDetailMembersFragment -> {
                    val argument = NavArgument.Builder()
                        .setDefaultValue(args.groupName)
                        .setType(NavType.StringType)
                        .build()
                    destination.addArgument("groupName", argument)
                }
            }
        }
        val bottomNav = binding.groupsDetailBottomNav
        bottomNav.setupWithNavController(navController)
    }
}