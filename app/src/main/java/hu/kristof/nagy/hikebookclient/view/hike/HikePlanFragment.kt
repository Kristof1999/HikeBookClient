package hu.kristof.nagy.hikebookclient.view.hike

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentHikePlanBinding
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.hike.HikePlanViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@AndroidEntryPoint
class HikePlanFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentHikePlanBinding
    private val viewModel: HikePlanViewModel by viewModels()
    private lateinit var map: MapView

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

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.hikePlanMap
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()

        binding.hikePlanTransportMeanSpinner.onItemSelectedListener = this
        SpinnerUtils.setTransportSpinnerAdapter(requireContext(), binding.hikePlanTransportMeanSpinner)

        binding.hikePlanStartButton.setOnClickListener {
            if (viewModel.transportType != TransportType.NOTHING) {
                val startPoint = Point(
                    viewModel.startPoint.latitude, viewModel.startPoint.longitude,
                    MarkerType.SET, ""
                )
                val endPoint = Point(
                    viewModel.endPoint.latitude, viewModel.endPoint.longitude,
                    MarkerType.NEW, ""
                )
                val transportType = viewModel.transportType
                val directions = HikePlanFragmentDirections
                    .actionHikePlanFragmentToHikeTransportFragment(startPoint, endPoint, transportType)
                findNavController().navigate(directions)
            } else {
                // go directly to hike fragment
            }
        }
        binding.hikePlanDateButton.setOnClickListener {
            //val datePickerFragment = DatePickerFragment()
            //datePickerFragment.show(parentFragmentManager, "datePicker")

            binding.lifecycleOwner = viewLifecycleOwner
//            datePickerFragment.yearRes.observe(viewLifecycleOwner) {
//                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
//            }
//            datePickerFragment.monthRes.observe(viewLifecycleOwner) {
//                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
//            }
//            datePickerFragment.dayRes.observe(viewLifecycleOwner) {
//                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
//            }
            viewModel.forecast()
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.forecastRes.observe(viewLifecycleOwner) {
            binding.hikePlanWeatherTv.text = it
        }

        binding.hikePlanStartSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setStartTo(isChecked)
        }
        binding.hikePlanDestinationSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDestinationTo(isChecked)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.switchOffStart.observe(viewLifecycleOwner) {
            binding.hikePlanStartSwitch.isChecked = false
        }
        viewModel.switchOffEnd.observe(viewLifecycleOwner) {
            binding.hikePlanDestinationSwitch.isChecked = false
        }
        val startMarker = Marker(map)
        startMarker.position = viewModel.startPoint
        startMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        startMarker.icon = MarkerUtils.getMarkerIcon(MarkerType.SET, requireContext())
        map.overlays.add(startMarker)
        viewModel.startPointChanged.observe(viewLifecycleOwner) {
            startMarker.position = viewModel.startPoint
            map.invalidate()
        }
        val endMarker = Marker(map)
        endMarker.position = viewModel.endPoint
        endMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        endMarker.icon = MarkerUtils.getMarkerIcon(MarkerType.NEW, requireContext())
        map.overlays.add(endMarker)
        viewModel.endPointChanged.observe(viewLifecycleOwner) {
            endMarker.position = viewModel.endPoint
            map.invalidate()
        }

        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                viewModel.onSingleTap(p!!)
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return true
            }
        })
        map.overlays.add(0, mapEventsOverlay)
        map.invalidate()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        SpinnerUtils.onTransportItemSelected(pos, viewModel)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MapUtils.onRequestPermissionsResult(
            requestCode, permissions, grantResults, requireActivity()
        )
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}