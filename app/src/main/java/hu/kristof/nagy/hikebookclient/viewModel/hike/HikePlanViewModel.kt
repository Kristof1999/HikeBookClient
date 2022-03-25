package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.ViewModel
import hu.kristof.nagy.hikebookclient.view.hike.TransportType
import org.osmdroid.util.GeoPoint

class HikePlanViewModel : ViewModel() {
    var transportType = TransportType.NOTHING
    var startPoint: GeoPoint? = null
    var destinationPPoint: GeoPoint? = null


}