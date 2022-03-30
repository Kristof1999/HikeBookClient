package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.print.PrintHelper
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentMyMapDetailBinding
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setMapCenterOnPolylineCenter
import hu.kristof.nagy.hikebookclient.util.setZoomForPolyline
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
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
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_map_detail, container, false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()

        val viewModel: MyMapViewModel by activityViewModels()
        val args: MyMapDetailFragmentArgs by navArgs()
        binding.myMapDetailRouteNameTv.text = args.userRoute.routeName
        val polyline = args.userRoute.toPolyline()
        map.setMapCenterOnPolylineCenter(polyline)
        map.setZoomForPolyline(polyline)
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
                .actionMyMapDetailFragmentToRouteEditFragment(args.userRoute)
            findNavController().navigate(action)
        }
        binding.myMapDetailDeleteButton.setOnClickListener {
            viewModel.deleteRoute(args.userRoute.routeName)
        }
        binding.myMapDetailPrintButton.setOnClickListener {
            val bitmap = map.drawToBitmap()
            PrintHelper(requireContext()).printBitmap(args.userRoute.routeName, bitmap)
        }
    }

    private fun onDeleteResult(res: Result<Boolean>) {
        handleResult(context, res) {
            findNavController().navigate(
                R.id.action_myMapDetailFragment_to_myMapListFragment
            )
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.MY_MAP_DETAIL
            val action = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(action)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}