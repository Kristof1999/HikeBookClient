package hu.kristof.nagy.hikebookclient.view.hike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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
    private lateinit var binding: FragmentHikePlanStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_hike_plan_start, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: HikePlanDateViewModel by viewModels()
        setupForecasting(viewModel)

        val args: HikePlanStartFragmentArgs by navArgs()
        handleOfflineLoad(requireContext()) {
            viewModel.loadRoute(args.routeName)
        }

        setClickListeners(args)
    }

    private fun setClickListeners(args: HikePlanStartFragmentArgs) = with(binding) {
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
        viewModel: HikePlanDateViewModel
    ) {
        var date: String? = null
        var hour: Int? = null
        binding.lifecycleOwner = viewLifecycleOwner

        val datePickerFragment = DatePickerFragment()
        datePickerFragment.dateRes.observe(viewLifecycleOwner) { dateRes ->
            date = dateRes
            hour?.let {
                catchAndShowIllegalStateAndArgument(context) {
                    viewModel.forecast(dateRes, it)
                }
            }
        }
        binding.hikePlanStartDatePickerButton.setOnClickListener {
            handleOffline(requireContext()) {
                datePickerFragment.show(parentFragmentManager, "datePicker")
            }
        }

        val timePickerFragment = TimePickerFragment()
        binding.lifecycleOwner = viewLifecycleOwner
        timePickerFragment.timeRes.observe(viewLifecycleOwner) { hourRes ->
            hour = hourRes.get(Calendar.HOUR_OF_DAY)
            date?.let {
                catchAndShowIllegalStateAndArgument(context) {
                    viewModel.forecast(it, hourRes.get(Calendar.HOUR_OF_DAY))
                }
            }
        }
        binding.hikePlanStartTimePickerButton.setOnClickListener {
            handleOffline(requireContext()) {
                timePickerFragment.show(parentFragmentManager, "timePicker")
            }
        }

        viewModel.forecastRes.observe(viewLifecycleOwner) {
            binding.hikePlanStartTv.text = it
        }
    }
}