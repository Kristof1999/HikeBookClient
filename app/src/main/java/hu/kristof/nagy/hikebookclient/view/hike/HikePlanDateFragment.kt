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
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikePlanDateBinding
import hu.kristof.nagy.hikebookclient.util.handleIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikePlanDateViewModel
import java.util.*

@AndroidEntryPoint
class HikePlanDateFragment : Fragment() {
    private lateinit var binding: FragmentHikePlanDateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_hike_plan_date, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: HikePlanDateViewModel by viewModels()
        setupForecasting(viewModel)

        val args: HikePlanDateFragmentArgs by navArgs()
        viewModel.loadRoute(args.routeName)

        setClickListeners(args)
    }

    private fun setClickListeners(args: HikePlanDateFragmentArgs) = with(binding) {
        hikePlanDateTransportPlanButton.setOnClickListener {
            val isForward = true
            val directions = HikePlanDateFragmentDirections
                .actionHikePlanDateFragmentToHikePlanTransportFragment(isForward, args.routeName)
            findNavController().navigate(directions)
        }
        hikePlanDateHikeStartButton.setOnClickListener {
            val directions = HikePlanDateFragmentDirections
                .actionHikePlanDateFragmentToHikeFragment(args.routeName)
            findNavController().navigate(directions)
        }
    }

    private fun setupForecasting(
        viewModel: HikePlanDateViewModel
    ) {
        // TODO: refactor by using one calendar here, example: myMapList
        var date: String? = null
        var hour: Int? = null
        val datePickerFragment = DatePickerFragment()
        binding.lifecycleOwner = viewLifecycleOwner
        datePickerFragment.dateRes.observe(viewLifecycleOwner) { dateRes ->
            date = dateRes
            hour?.let {
                handleIllegalStateAndArgument(context) {
                    viewModel.forecast(dateRes, it)
                }
            }
        }
        binding.hikePlanDateDatePickerButton.setOnClickListener {
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }
        binding.hikePlanDateTimePickerButton.setOnClickListener {
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.show(parentFragmentManager, "timePicker")

            binding.lifecycleOwner = viewLifecycleOwner
            timePickerFragment.timeRes.observe(viewLifecycleOwner) { hourRes ->
                hour = hourRes.get(Calendar.HOUR_OF_DAY)
                date?.let {
                    handleIllegalStateAndArgument(context) {
                        viewModel.forecast(it, hourRes.get(Calendar.HOUR_OF_DAY))
                    }
                }
            }
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.forecastRes.observe(viewLifecycleOwner) {
            binding.hikePlanDateTw.text = it
        }
    }
}