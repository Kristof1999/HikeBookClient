package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.print.PrintHelper
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentMyMapDetailBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.*
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.view.hike.TimePickerFragment
import hu.kristof.nagy.hikebookclient.view.routes.TextDialogFragment
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapDetailViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import java.util.*

/**
 * A MapFragment to display the details of the chosen route.
 * It displays the route on a map, and the route's name.
 * It has several buttons:
 * one to start hiking,
 * with others, the user can edit/delete/print the given route,
 * and the user can also create a group hike with another button.
 */
@AndroidEntryPoint
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

        val viewModel: MyMapDetailViewModel by viewModels()
        val args: MyMapDetailFragmentArgs by navArgs()

        setupLoad(viewModel, args)

        setClickListeners(args, viewModel)

        viewModel.deleteRes.observe(viewLifecycleOwner) {
            onDeleteResult(it)
        }
    }

    private fun setupLoad(
        viewModel: MyMapDetailViewModel,
        args: MyMapDetailFragmentArgs
    ) {
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.route.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { userRoute ->
                adaptView(args, userRoute)
            }
        }
        handleOfflineLoad(requireContext()) {
            viewModel.loadUserRoute(args.routeName)
        }
    }

    private fun adaptView(args: MyMapDetailFragmentArgs, route: Route) {
        binding.myMapDetailRouteNameTv.text = args.routeName
        val polyline = route.toPolyline()
        map.apply {
            setMapCenterOnPolylineCenter(polyline)
            setZoomForPolyline(polyline)
            overlays.add(polyline)
        }
        map.invalidate()
    }

    private fun setClickListeners(
        args: MyMapDetailFragmentArgs,
        viewModel: MyMapDetailViewModel
    ) = with(binding) {
        myMapDetailEditButton.setOnClickListener {
            val directions = MyMapDetailFragmentDirections
                .actionMyMapDetailFragmentToRouteEditFragment(RouteType.USER, null, args.routeName)
            findNavController().navigate(directions)
        }
        myMapDetailDeleteButton.setOnClickListener {
            handleOffline(requireContext()) {
                viewModel.deleteRoute(args.routeName)
            }
        }
        myMapDetailPrintButton.setOnClickListener {
            val bitmap = map.drawToBitmap()
            PrintHelper(requireContext()).printBitmap(args.routeName, bitmap)
        }
        myMapDetailHikePlanFab.setOnClickListener {
            val directions = MyMapDetailFragmentDirections
                .actionMyMapDetailFragmentToHikePlanDateFragment(args.routeName)
            findNavController().navigate(directions)
        }
        myMapDetailGroupHikeCreateButton.setOnClickListener {
            handleOffline(requireContext()) {
                val dateTime = Calendar.getInstance().apply { clear() }

                // note to reader: read this block in reverse order
                // as that is the way in which the dialogs will be shown to the user
                val groupHikeCreateDialog = TextDialogFragment.instanceOf(
                    R.string.group_hike_create_text, R.string.groups_create_dialog_hint_text
                )
                groupHikeCreateDialog.text.observe(viewLifecycleOwner) { groupHikeName ->
                    catchAndShowIllegalStateAndArgument(requireContext()) {
                        viewModel.createGroupHike(dateTime, groupHikeName)
                    }
                }

                val timeDialog = TimePickerFragment()
                timeDialog.timeRes.observe(viewLifecycleOwner) { timeRes ->
                    dateTime.set(Calendar.HOUR_OF_DAY, timeRes.get(Calendar.HOUR_OF_DAY))
                    dateTime.set(Calendar.MINUTE, timeRes.get(Calendar.MINUTE))

                    groupHikeCreateDialog.show(parentFragmentManager, "group hike name")
                }

                val dateDialog = DatePickerFragment()
                binding.lifecycleOwner = viewLifecycleOwner
                dateDialog.dateRes.observe(viewLifecycleOwner) { dateRes ->
                    dateTime.set(Calendar.YEAR, dateRes.get(Calendar.YEAR))
                    dateTime.set(Calendar.MONTH, dateRes.get(Calendar.MONTH))
                    dateTime.set(Calendar.DAY_OF_MONTH, dateRes.get(Calendar.DAY_OF_MONTH))

                    timeDialog.show(parentFragmentManager, "group hike time")
                }

                dateDialog.show(parentFragmentManager, "group hike date")
            }
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
            setStartZoomAndCenter()
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