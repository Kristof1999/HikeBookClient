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
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikePlanDateViewModel

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
        val args: HikePlanDateFragmentArgs by navArgs()
        var date: String? = null
        var hour: Int? = null
        binding.hikePlanDateDatePickerButton.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")

            binding.lifecycleOwner = viewLifecycleOwner
            datePickerFragment.dateRes.observe(viewLifecycleOwner) { dateRes ->
                date = dateRes
                hour?.let {
                    viewModel.forecast(args.userRoute.points, dateRes, it)
                }
            }
        }
        binding.hikePlanDateTimePickerButton.setOnClickListener {
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.show(parentFragmentManager, "timePicker")

            binding.lifecycleOwner = viewLifecycleOwner
            timePickerFragment.hourRes.observe(viewLifecycleOwner) { hourRes ->
                hour = hourRes
                date?.let {
                    viewModel.forecast(args.userRoute.points, it, hourRes)
                }
            }
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.forecastRes.observe(viewLifecycleOwner) {
            binding.hikePlanDateTw.text = it
        }

        binding.hikePlanDateTransportPlanButton.setOnClickListener {
            val isForward = true
            val directions = HikePlanDateFragmentDirections
                .actionHikePlanDateFragmentToHikePlanTransportFragment(args.userRoute, isForward)
            findNavController().navigate(directions)
        }
        binding.hikePlanDateHikeStartButton.setOnClickListener {
            val directions = HikePlanDateFragmentDirections
                .actionHikePlanDateFragmentToHikeFragment(args.userRoute)
            findNavController().navigate(directions)
        }
    }
}