package hu.kristof.nagy.hikebookclient.view.browse

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentBrowseDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.viewModel.browse.BrowseDetailViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@AndroidEntryPoint
class BrowseDetailFragment : Fragment() {
    private lateinit var binding: FragmentBrowseDetailBinding
    private lateinit var map: MapView
    private val args: BrowseDetailFragmentArgs by navArgs()

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
        val mapController = map.controller
        mapController.setZoom(Constants.START_ZOOM)
        mapController.setCenter(Constants.START_POINT)

        binding.browseDetailHikeDescriptionTv.text =
            "Felhasználói név: ${args.userName}\nÚtvonal név: ${args.routeName}"

        val viewModel: BrowseDetailViewModel by viewModels()
        viewModel.loadPoints(args.userName, args.routeName)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.points.observe(viewLifecycleOwner) { points ->
            val polyline = Polyline()
            polyline.setPoints(points.map { point ->
                point.toGeoPoint()
            })
            map.overlays.add(polyline)
            map.invalidate()
        }
    }
}