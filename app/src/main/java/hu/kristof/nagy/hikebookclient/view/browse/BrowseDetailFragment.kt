package hu.kristof.nagy.hikebookclient.view.browse

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentBrowseDetailBinding
import hu.kristof.nagy.hikebookclient.util.Constants
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

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
    }
}