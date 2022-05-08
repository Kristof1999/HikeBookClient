package hu.kristof.nagy.hikebookclient.view.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHelpBinding

/**
 * A Fragment which displays help information for a given page.
 */
class HelpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHelpBinding.inflate(inflater, container, false)
        val args: HelpFragmentArgs by navArgs()

        binding.helpTextView.text = when (args.helpRequestType) {
            HelpRequestType.MY_MAP -> getString(R.string.help_my_map)
            HelpRequestType.MY_MAP_LIST -> getString(R.string.help_my_map_list)
            HelpRequestType.MY_MAP_DETAIL -> getString(R.string.help_my_map_detail)
            HelpRequestType.ROUTE_CREATE -> getString(R.string.help_route_create)
            HelpRequestType.ROUTE_EDIT -> getString(R.string.help_route_edit)
            HelpRequestType.BROWSE_LIST -> getString(R.string.help_browse_list)
            HelpRequestType.BROWSE_DETAIL -> getString(R.string.help_browse_detail)
            HelpRequestType.GROUP_HIKE_DETAIL -> getString(R.string.help_group_hike_detail)
            HelpRequestType.GROUP_HIKE -> getString(R.string.help_group_hike)
            HelpRequestType.GROUPS -> getString(R.string.help_groups)
            HelpRequestType.GROUPS_DETAIL_MAP -> getString(R.string.help_groups_detail_map)
            HelpRequestType.GROUPS_DETAIL_LIST -> getString(R.string.help_groups_detail_list)
            HelpRequestType.GROUPS_DETAIL_MEMBERS -> getString(R.string.help_groups_detail_members)
            HelpRequestType.HIKE_PLAN_START -> getString(R.string.help_hike_plan_start)
            HelpRequestType.HIKE_PLAN_TRANSPORT -> getString(R.string.help_hike_plan_transport)
            HelpRequestType.HIKE_TRANSPORT -> getString(R.string.help_hike_transport)
            HelpRequestType.HIKE -> getString(R.string.help_hike)
        }

        return binding.root
    }
}