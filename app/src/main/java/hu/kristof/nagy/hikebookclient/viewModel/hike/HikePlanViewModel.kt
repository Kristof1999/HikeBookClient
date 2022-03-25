package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.ViewModel
import hu.kristof.nagy.hikebookclient.view.hike.TransportType

class HikePlanViewModel : ViewModel() {
    var transportType = TransportType.NOTHING
}