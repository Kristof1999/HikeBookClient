package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kristof.nagy.hikebookclient.view.hike.HikeTransportFragmentArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint

class HikeTransportViewModel : ViewModel() {

    private var _roadRes = MutableLiveData<Road>()
    val roadRes: LiveData<Road>
        get() = _roadRes

    fun getRoad(args: HikeTransportFragmentArgs, roadManager: RoadManager) {
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