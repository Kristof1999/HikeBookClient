package hu.kristof.nagy.hikebookclient.view.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentBrowseDetailBinding
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.browse.BrowseDetailViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Polyline

/**
 * A Fragment to display the details of a route while browsing.
 */
@AndroidEntryPoint
class BrowseDetailFragment : MapFragment() {
    private lateinit var binding: FragmentBrowseDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_browse_detail, container, false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()

        val args: BrowseDetailFragmentArgs by navArgs()

        val viewModel: BrowseDetailViewModel by viewModels()
        handleOfflineLoad(requireContext()) {
            viewModel.loadDetails(args.userName, args.routeName)
            // TODO: add listener for when the device is online, we load the details
        }

        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.route.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { route ->
                onPointsLoad(route.points)
                binding.browseDetailHikeDescriptionTv.text =
                    getString(R.string.browse_hike_detail_description,
                        args.userName, args.routeName, route.description
                    )
            }
        }
        binding.browseDetailAddToMyMapButton.setOnClickListener {
            onAddToMyMap(viewModel, args)
        }
        viewModel.addRes.observe(viewLifecycleOwner) {
            onAddResult(it)
        }
    }

    private fun onAddResult(res: Result<Boolean>) {
        handleResult(context, res) {
            findNavController().navigate(
                R.id.action_browseDetailFragment_to_browseListFragment
            )
        }
    }

    private fun onAddToMyMap(
        viewModel: BrowseDetailViewModel,
        args: BrowseDetailFragmentArgs
    ) {
        catchAndShowIllegalStateAndArgument(requireContext()) {
            handleOffline(requireContext()) {
                viewModel.addToMyMap(args.routeName)
            }
        }
    }

    private fun onPointsLoad(points: List<Point>) {
        val polyline = Polyline()
        polyline.setPoints(points.map { point ->
            point.toGeoPoint()
        })
        map.setMapCenterOnPolylineCenter(polyline)
        map.overlays.add(polyline)
        map.invalidate()
    }

    private fun initMap() {
        map = binding.browseDetailMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
            addCopyRightOverlay()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.BROWSE_DETAIL
            val action = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(action)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}