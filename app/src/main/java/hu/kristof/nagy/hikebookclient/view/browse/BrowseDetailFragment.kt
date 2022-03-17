package hu.kristof.nagy.hikebookclient.view.browse

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentBrowseDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.util.MapUtils
import hu.kristof.nagy.hikebookclient.util.addCopyRightOverlay
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import hu.kristof.nagy.hikebookclient.viewModel.browse.BrowseDetailViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

/**
 * A Fragment to display the details of a route while browsing.
 */
@AndroidEntryPoint
class BrowseDetailFragment : Fragment() {
    private lateinit var binding: FragmentBrowseDetailBinding
    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_browse_detail, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        map = binding.browseDetailMap
        map.setStartZoomAndCenter()
        map.addCopyRightOverlay()

        val args: BrowseDetailFragmentArgs by navArgs()
        binding.browseDetailHikeDescriptionTv.text =
            getString(R.string.browse_hike_detail_description, args.userName, args.routeName)

        val viewModel: BrowseDetailViewModel by viewModels()
        viewModel.loadPoints(args.userName, args.routeName)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.points.observe(viewLifecycleOwner) { points ->
            val polyline = Polyline()
            polyline.setPoints(points.map { point ->
                point.toGeoPoint()
            })
            map.controller.setCenter(polyline.bounds.centerWithDateLine)
            map.overlays.add(polyline)
            map.invalidate()
        }
        binding.browseDetailAddToMyMapButton.setOnClickListener {
            try {
                viewModel.addToMyMap(args.routeName)
            } catch(e: IllegalStateException) {
                Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.addRes.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(
                    R.id.action_browseDetailFragment_to_browseListFragment
                )
            } else {
                Toast.makeText(
                    context, getText(R.string.generic_error_msg), Toast.LENGTH_LONG
                ).show()
            }
        }
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