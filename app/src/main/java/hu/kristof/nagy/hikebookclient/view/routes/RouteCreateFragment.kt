// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki
// spinner: https://developer.android.com/guide/topics/ui/controls/spinner

package hu.kristof.nagy.hikebookclient.view.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentRouteCreateBinding
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.SpinnerUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteCreateViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

/**
 * A Fragment to create a route for the logged in user.
 */
@AndroidEntryPoint
class RouteCreateFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var map: MapView
    private lateinit var binding: FragmentRouteCreateBinding
    private val viewModel: RouteCreateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_route_create, container, false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()

        binding.routeCreateMarkerSpinner.onItemSelectedListener = this
        SpinnerUtils.setMarkerSpinnerAdapter(requireContext(), binding.routeCreateMarkerSpinner)

        val args: RouteCreateFragmentArgs by navArgs()
        binding.routeCreateCreateButton.setOnClickListener {
            onCreate(args, viewModel)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routeCreateRes.observe(viewLifecycleOwner) {
            onRouteCreateResult(args.routeType, it)
            // névnek egyedinek kell lennie
        }

        MapUtils.setMapClickListeners(requireContext(), map, binding.routeCreateDeleteSwitch, viewModel)
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        SpinnerUtils.onMarkerItemSelected(pos, viewModel, parentFragmentManager, viewLifecycleOwner)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
    }

    private fun onCreate(args: RouteCreateFragmentArgs, viewModel: RouteCreateViewModel) {
        try {
            viewModel.onRouteCreate(
                args,
                binding.routeCreateRouteNameEditText.text.toString(),
                binding.routeCreateHikeDescriptionEditText.text.toString()
            )
        } catch (e: IllegalArgumentException) {
            Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onRouteCreateResult(routeType: RouteType, res: Result<Boolean>) {
        handleResult(context, res) {
            when (routeType) {
                RouteType.USER -> findNavController().navigate(
                    R.id.action_routeCreateFragment_to_myMapFragment
                )
                RouteType.GROUP -> findNavController().navigate(
                    R.id.action_routeCreateFragment_to_groupsDetailFragment
                )
                // TODO: update
                RouteType.GROUP_HIKE -> findNavController().navigate(
                    R.id.action_routeCreateFragment_to_groupsDetailFragment
                )
            }

        }
    }

    private fun initMap() {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        map = binding.routeCreateMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()
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
            val requestType = HelpRequestType.ROUTE_CREATE
            val action = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(action)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}