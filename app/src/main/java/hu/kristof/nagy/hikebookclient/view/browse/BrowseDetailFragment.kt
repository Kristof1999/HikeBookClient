package hu.kristof.nagy.hikebookclient.view.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
 * A MapFragment that displays the details of a browse list item.
 * It shows the route on a map, and
 * displays the route's name, the corresponding user name,
 * and the route's description.
 * Has a button, with which the user can
 * add the current route to his/her map.
 */
@AndroidEntryPoint
class BrowseDetailFragment : MapFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val browseDetailViewModel: BrowseDetailViewModel by viewModels()
        val args: BrowseDetailFragmentArgs by navArgs()

        val binding = FragmentBrowseDetailBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = browseDetailViewModel
                context = requireContext()
                routeName = args.routeName
                executePendingBindings()
            }

        setupObservers(browseDetailViewModel, args, binding)

        initMap(binding)

        setupLoad(browseDetailViewModel, args)

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObservers(
        viewModel: BrowseDetailViewModel,
        args: BrowseDetailFragmentArgs,
        binding: FragmentBrowseDetailBinding
    ) {
        viewModel.addRes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) {
                Toast.makeText(context, "A hozzáadás sikeres!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(
                    R.id.action_browseDetailFragment_to_browseListFragment
                )
            }
        }
        viewModel.route.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { route ->
                onPointsLoad(route.points)
            }
        }
    }

    private fun setupLoad(
        viewModel: BrowseDetailViewModel,
        args: BrowseDetailFragmentArgs
    ) {
        handleOfflineLoad(requireContext()) {
            viewModel.loadDetails(args.userName, args.routeName, requireContext().resources)
            // TODO: add listener for when the device is online, we load the details
        }
    }

    private fun onPointsLoad(points: List<Point>) = Polyline().apply {
        setPoints(points.map { point ->
            point.toGeoPoint()
        })
    }.also { polyline ->
        with(map) {
            setMapCenterOnPolylineCenter(polyline)
            overlays.add(polyline)
            invalidate()
        }
    }

    private fun initMap(binding: FragmentBrowseDetailBinding) {
        map = binding.browseDetailMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
            addCopyRightOverlay()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.BROWSE_DETAIL
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}