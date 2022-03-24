package hu.kristof.nagy.hikebookclient.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHelpBinding
import hu.kristof.nagy.hikebookclient.model.HelpRequestType

class HelpFragment : Fragment() {
    private lateinit var binding: FragmentHelpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_help, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: HelpFragmentArgs by navArgs()
        binding.helpTextView.text = when (args.helpRequestType) {
            HelpRequestType.MY_MAP -> getString(R.string.help_my_map)
            HelpRequestType.MY_MAP_LIST -> getString(R.string.help_my_map_list)
            HelpRequestType.MY_MAP_DETAIL -> getString(R.string.help_my_map_detail)
            HelpRequestType.ROUTE_CREATE -> getString(R.string.help_route_create)
            HelpRequestType.ROUTE_EDIT -> getString(R.string.help_route_edit)
            HelpRequestType.BROWSE_LIST -> getString(R.string.help_browse_list)
            HelpRequestType.BROWSE_DETAIL -> getString(R.string.help_browse_detail)
        }
    }
}