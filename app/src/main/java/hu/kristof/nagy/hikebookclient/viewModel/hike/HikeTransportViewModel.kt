package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kristof.nagy.hikebookclient.view.hike.HikeTransportFragmentArgs
import hu.kristof.nagy.hikebookclient.view.hike.TransportType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.util.GeoPoint

class HikeTransportViewModel : ViewModel() {

    private var _roadRes = MutableLiveData<Road>()
    val roadRes: LiveData<Road>
        get() = _roadRes

    fun getRoad(args: HikeTransportFragmentArgs, roadManager: OSRMRoadManager) {
        when (args.transportType) {
            TransportType.CAR -> roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR)
            TransportType.BICYCLE -> roadManager.setMean(OSRMRoadManager.MEAN_BY_BIKE)
        }

        val wayPoints = ArrayList<GeoPoint>()
        wayPoints.add(args.startPoint.toGeoPoint())
        wayPoints.add(args.endPoint.toGeoPoint())
        val deferred = viewModelScope.async(Dispatchers.IO) {
             roadManager.getRoad(wayPoints)
        }
        viewModelScope.launch {
            _roadRes.value = deferred.await()
        }
    }
}