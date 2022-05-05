package hu.kristof.nagy.hikebookclient.view.hike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikePlanStartBinding
import hu.kristof.nagy.hikebookclient.util.catchAndShowIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikePlanDateViewModel
import java.util.*

/**
 * A Fragment to start planning the hike.
 * The user can choose the date and time of the hike,
 * and get a weather forecast.
 * The user can choose to plan his/her way to the start point of the hike,
 * or start hiking immediately.
 */
@AndroidEntryPoint
class HikePlanStartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHikePlanStartBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        val viewModel: HikePlanDateViewModel by viewModels()

        viewModel.forecastRes.observe(viewLifecycleOwner) {
            binding.hikePlanStartTv.text = it
        }

        setupForecasting(viewModel, binding)

        val args: HikePlanStartFragmentArgs by navArgs()
        handleOfflineLoad(requireContext()) {
            viewModel.loadRoute(args.routeName)
        }

        setClickListeners(args, binding)

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setClickListeners(
        args: HikePlanStartFragmentArgs,
        binding: FragmentHikePlanStartBinding
    ) = with(binding) {
        hikePlanStartTransportPlanButton.setOnClickListener {
            val isForward = true
            val directions = HikePlanStartFragmentDirections
                .actionHikePlanDateFragmentToHikePlanTransportFragment(isForward, args.routeName)
            findNavController().navigate(directions)
        }
        hikePlanStartHikeStartButton.setOnClickListener {
            val directions = HikePlanStartFragmentDirections
                .actionHikePlanDateFragmentToHikeFragment(args.routeName)
            findNavController().navigate(directions)
        }
    }

    private fun setupForecasting(
        viewModel: HikePlanDateViewModel,
        binding: FragmentHikePlanStartBinding
    ) {
        var date: String? = null
        var hour: Int? = null

        val datePickerFragment = DatePickerFragment()
        datePickerFragment.dateRes.observe(viewLifecycleOwner) { dateRes ->
            date = dateRes
            hour?.let {
                catchAndShowIllegalStateAndArgument(context) {
                    try {
                        viewModel.forecast(dateRes, it)
                    } catch (e: IndexOutOfBoundsException) {
                        Toast.makeText(context, "Válassz más időpontot!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.hikePlanStartDatePickerButton.setOnClickListener {
            handleOffline(requireContext()) {
                datePickerFragment.show(parentFragmentManager, "datePicker")
            }
        }

        val timePickerFragment = TimePickerFragment()
        timePickerFragment.timeRes.observe(viewLifecycleOwner) { hourRes ->
            hour = hourRes.get(Calendar.HOUR_OF_DAY)
            date?.let {
                catchAndShowIllegalStateAndArgument(context) {
                    try {
                        viewModel.forecast(it, hourRes.get(Calendar.HOUR_OF_DAY))
                    } catch (e: IndexOutOfBoundsException) {
                        Toast.makeText(context, "Válassz más időpontot!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.hikePlanStartTimePickerButton.setOnClickListener {
            handleOffline(requireContext()) {
                timePickerFragment.show(parentFragmentManager, "timePicker")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.HIKE_PLAN_START
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}