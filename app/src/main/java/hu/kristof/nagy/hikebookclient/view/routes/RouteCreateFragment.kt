// based on the osmdroid tutorial:
// https://github.com/osmdroid/osmdroid/wiki
// spinner: https://developer.android.com/guide/topics/ui/controls/spinner

package hu.kristof.nagy.hikebookclient.view.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentRouteCreateBinding
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.catchAndShowIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.routes.OnSingleTapHandlerTextMarkerTypeDecorator
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteCreateViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

/**
 * A MapFragment to create a route for the logged in user.
 * The user can set the name of the route and it's description in editTexts,
 * and points of the route on a map.
 * The user can choose between which type of point to place next with a spinner.
 * With a switch, the user can decide, if he/she wants to delete the last placed marker.
 * With the create button, the user can save the route.
 */
@AndroidEntryPoint
class RouteCreateFragment : RouteFragment() {
    private lateinit var binding: FragmentRouteCreateBinding
    override val viewModel: RouteCreateViewModel by viewModels()
    override val switch: SwitchCompat by lazy {
        binding.routeCreateDeleteSwitch
    }
    private val args: RouteCreateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate<FragmentRouteCreateBinding>(
            inflater, R.layout.fragment_route_create, container, false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        setupObservers()

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObservers() {
        OnSingleTapHandlerTextMarkerTypeDecorator
            .setSpinnerToDefault.observe(viewLifecycleOwner) {
                binding.routeCreateMarkerSpinner.setSelection(0)
            }
        viewModel.routeCreateRes.observe(viewLifecycleOwner) {
            onRouteCreateResult(it, args)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()

        setupSpinner()

        setupRouteCreate(args)

        setMapClickListeners(requireContext(), map, binding.routeCreateDeleteSwitch, viewModel)
    }

    private fun setupSpinner() {
        binding.routeCreateMarkerSpinner.onItemSelectedListener = this
        setMarkerSpinnerAdapter(requireContext(), binding.routeCreateMarkerSpinner)
    }

    private fun setupRouteCreate(args: RouteCreateFragmentArgs) {
        binding.routeCreateCreateButton.setOnClickListener {
            handleOffline(requireContext()) {
                onRouteCreate(args, viewModel)
            }
        }
    }

    private fun onRouteCreate(args: RouteCreateFragmentArgs, viewModel: RouteCreateViewModel) {
        catchAndShowIllegalStateAndArgument(requireContext()) {
            viewModel.onRouteCreate(
                args,
                binding.routeCreateRouteNameEditText.text.toString(),
                binding.routeCreateHikeDescriptionEditText.text.toString()
            )
        }
    }

    private fun onRouteCreateResult(
        res: ResponseResult<Boolean>,
        args: RouteCreateFragmentArgs
    ) {
        handleResult(context, res) {
            when (args.routeType) {
                RouteType.USER -> findNavController().navigate(
                    R.id.action_routeCreateFragment_to_myMapFragment
                )
                RouteType.GROUP -> {
                    val groupName = args.groupName!!
                    // isConnectedPage is true because
                    // only a connected member can create routes
                    val directions = RouteCreateFragmentDirections
                        .actionRouteCreateFragmentToGroupsDetailFragment(groupName, true)
                    findNavController().navigate(directions)
                }
            }
        }
    }

    private fun initMap() {
        map = binding.routeCreateMap.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setStartZoomAndCenter()
            addCopyRightOverlay()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.helpMenuItem) {
            val requestType = HelpRequestType.ROUTE_CREATE
            val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
            findNavController().navigate(directions)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}