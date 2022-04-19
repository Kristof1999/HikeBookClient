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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentRouteCreateBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
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
class RouteCreateFragment : MapFragment(), AdapterView.OnItemSelectedListener {
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

        setupSpinner()

        val args: RouteCreateFragmentArgs by navArgs()

        setupRouteCreate(args)

        setMapClickListeners(requireContext(), map, binding.routeCreateDeleteSwitch, viewModel)
    }

    private fun setupSpinner() {
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.setSpinnerToDefault.observe(viewLifecycleOwner) {
            binding.routeCreateMarkerSpinner.setSelection(0)
        }
        binding.routeCreateMarkerSpinner.onItemSelectedListener = this
        setMarkerSpinnerAdapter(requireContext(), binding.routeCreateMarkerSpinner)
    }

    private fun setupRouteCreate(args: RouteCreateFragmentArgs) {
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routeCreateRes.observe(viewLifecycleOwner) {
            onRouteCreateResult(it, args)
        }
        binding.routeCreateCreateButton.setOnClickListener {
            handleOffline(requireContext()) {
                onRouteCreate(args, viewModel)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        onMarkerItemSelected(pos, viewModel, parentFragmentManager, viewLifecycleOwner)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // keep type as is
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
        res: Result<Boolean>,
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