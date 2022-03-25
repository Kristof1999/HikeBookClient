package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.kristof.nagy.hikebookclient.view.hike.TransportType
import org.osmdroid.util.GeoPoint

class HikePlanViewModel : ViewModel() {
    var transportType = TransportType.NOTHING

    var startPoint: GeoPoint? = null
    var destinationPPoint: GeoPoint? = null

    private var _switchOffStart = MutableLiveData(false)
    val switchOffStart: LiveData<Boolean>
        get() = _switchOffStart

    private var _switchOffDestination = MutableLiveData(false)
    val switchOffDestination: LiveData<Boolean>
        get() = _switchOffDestination

    /**
     * Indicates if we want to put the start point on the map.
     */
    private var isStart = false
    /**
     * Indicates if we want to put the destination point on the map.
     */
    private var isDestination = false

    fun setStartTo(value: Boolean) {
        if (value && isDestination) {
            isStart = value
            _switchOffDestination.value = !_switchOffDestination.value!!
        } else {
            isStart = value
        }
    }

    fun setDestinationTo(value: Boolean) {
        if (value && isStart) {
            isDestination = value
            _switchOffStart.value = !_switchOffStart.value!!
        } else {
            isDestination = value
        }
    }
}