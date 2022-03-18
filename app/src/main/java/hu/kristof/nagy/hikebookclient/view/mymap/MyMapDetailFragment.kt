package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.print.PrintHelper
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentMyMapDetailBinding
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

/**
 * A Fragment to display the details of the chosen route.
 */
class MyMapDetailFragment : Fragment() {
    private lateinit var binding: FragmentMyMapDetailBinding
    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_map_detail, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()

        val viewModel: MyMapViewModel by activityViewModels()
        val args: MyMapDetailFragmentArgs by navArgs()
        binding.myMapDetailRouteNameTv.text = args.route.routeName
        val polyline = args.route.toPolyline()
        val mapController = map.controller
        mapController.setCenter(polyline.bounds.centerWithDateLine)
        mapController.setZoom(Constants.START_ZOOM)
        map.overlays.add(polyline)
        map.invalidate()

        setClickListeners(args, viewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.deleteRes.observe(viewLifecycleOwner) {
            onDeleteResult(it)
        }
    }

    private fun setClickListeners(
        args: MyMapDetailFragmentArgs,
        viewModel: MyMapViewModel
    ) {
        binding.myMapDetailEditButton.setOnClickListener {
            val action = MyMapDetailFragmentDirections
                .actionMyMapDetailFragmentToRouteEditFragment(args.route)
            findNavController().navigate(action)
        }
        binding.myMapDetailDeleteButton.setOnClickListener {
            viewModel.deleteRoute(args.route.routeName)
        }
        binding.myMapDetailPrintButton.setOnClickListener {
            val bitmap = map.drawToBitmap()
            PrintHelper(requireContext()).printBitmap(args.route.routeName, bitmap)
        }
    }

    private fun onDeleteResult(it: Boolean) {
        if (it)
            findNavController().navigate(
                R.id.action_myMapDetailFragment_to_myMapListFragment
            )
        else
            Toast.makeText(
                context, resources.getText(R.string.generic_error_msg), Toast.LENGTH_SHORT
            ).show()
    }

    private fun initMap() {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.myMapDetailMap
        map.addCopyRightOverlay()
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