package hu.kristof.nagy.hikebookclient.view.hike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikePlanBinding
import hu.kristof.nagy.hikebookclient.util.SpinnerUtils
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikePlanViewModel

class HikePlanFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentHikePlanBinding
    private val viewModel: HikePlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_hike_plan, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.hikePlanMeanSpinner.onItemSelectedListener = this
        SpinnerUtils.setTransportSpinnerAdapter(requireContext(), binding.hikePlanMeanSpinner)

        binding.hikePlanDateButton.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")

            binding.lifecycleOwner = viewLifecycleOwner
            datePickerFragment.yearRes.observe(viewLifecycleOwner) {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
            datePickerFragment.monthRes.observe(viewLifecycleOwner) {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
            datePickerFragment.dayRes.observe(viewLifecycleOwner) {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        SpinnerUtils.onTransportItemSelected(pos, viewModel)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }
}