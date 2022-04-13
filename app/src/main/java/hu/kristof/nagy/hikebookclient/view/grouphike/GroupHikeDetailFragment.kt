package hu.kristof.nagy.hikebookclient.view.grouphike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupHikeDetailBinding
import hu.kristof.nagy.hikebookclient.util.MapFragment
import hu.kristof.nagy.hikebookclient.util.setStartZoomAndCenter
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

class GroupHikeDetailFragment : MapFragment() {
    private lateinit var binding: FragmentGroupHikeDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_group_hike_detail, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: GroupHikeDetailFragmentArgs by navArgs()
        binding.groupHikeDetailNameTv.text = args.groupHikeName
        binding.groupHikeDetailGeneralConnectButton.apply {
            if (args.isConnectedPage) {
                text = "Elhagyás"
            } else {
                text = "Csatlakozás"
            }
        }


        map = binding.groupHikeDetailMap.apply {
            setStartZoomAndCenter()
            setTileSource(TileSourceFactory.MAPNIK)
        }
    }
}