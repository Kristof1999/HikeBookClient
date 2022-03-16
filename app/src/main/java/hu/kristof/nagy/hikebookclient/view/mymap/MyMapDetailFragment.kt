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
import hu.kristof.nagy.hikebookclient.util.MapHelper
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

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
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.myMapDetailMap

        val mapController = map.controller
        // TODO: set center on center of route with appropriate zoom
        mapController.setZoom(Constants.START_ZOOM)
        mapController.setCenter(Constants.START_POINT)

        val viewModel: MyMapViewModel by activityViewModels()
        val args: MyMapDetailFragmentArgs by navArgs()
        binding.myMapDetailRouteNameTv.text = args.routeName
        val route = viewModel.routes.value!!.filter { route ->
            route.routeName == args.routeName
        }[0]
        val polyline = route.toPolyline()
        map.overlays.add(polyline)
        map.invalidate()

        binding.myMapDetailEditButton.setOnClickListener {
            val action = MyMapListFragmentDirections
                .actionMyMapListFragmentToRouteEditFragment(args.routeName)
            findNavController().navigate(action)
        }
        binding.myMapDetailDeleteButton.setOnClickListener {
            viewModel.deleteRoute(args.routeName)
        }
        binding.myMapDetailPrintButton.setOnClickListener {
            val bitmap = map.drawToBitmap()
            PrintHelper(requireContext()).printBitmap(args.routeName, bitmap)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.deleteRes.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(
                    R.id.action_myMapDetailFragment_to_myMapListFragment
                )
            else
                Toast.makeText(
                    context, resources.getText(R.string.generic_error_msg), Toast.LENGTH_SHORT
                ).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MapHelper.onRequestPermissionsResult(
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