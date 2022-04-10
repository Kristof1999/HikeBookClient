package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.print.PrintHelper
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentMyMapDetailBinding
import hu.kristof.nagy.hikebookclient.util.MapFragment
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setMapCenterOnPolylineCenter
import hu.kristof.nagy.hikebookclient.util.setZoomForPolyline
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

/**
 * A Fragment to display the details of the chosen route.
 */
class MyMapDetailFragment : MapFragment() {
    private lateinit var binding: FragmentMyMapDetailBinding

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
        adaptView(args)

        setClickListeners(args, viewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.deleteRes.observe(viewLifecycleOwner) {
            onDeleteResult(it)
        }
    }

    private fun adaptView(args: MyMapDetailFragmentArgs) {
        binding.myMapDetailRouteNameTv.text = args.route.routeName
        val polyline = args.route.toPolyline()
        with(map) {
            setMapCenterOnPolylineCenter(polyline)
            setZoomForPolyline(polyline)
            overlays.add(polyline)
            invalidate()
        }
    }

    private fun setClickListeners(
        args: MyMapDetailFragmentArgs,
        viewModel: MyMapViewModel
    ) = with(binding) {
        myMapDetailEditButton.setOnClickListener {
            val directions = MyMapDetailFragmentDirections
                .actionMyMapDetailFragmentToRouteEditFragment(args.route)
            findNavController().navigate(directions)
        }
        myMapDetailDeleteButton.setOnClickListener {
            viewModel.deleteRoute(args.route.routeName)
        }
        myMapDetailPrintButton.setOnClickListener {
            val bitmap = map.drawToBitmap()
            PrintHelper(requireContext()).printBitmap(args.route.routeName, bitmap)
        }
        myMapDetailHikePlanFab.setOnClickListener {
            val directions = MyMapDetailFragmentDirections
                .actionMyMapDetailFragmentToHikePlanDateFragment(args.route)
            findNavController().navigate(directions)
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
        map = binding.myMapDetailMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            addCopyRightOverlay()
        }
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