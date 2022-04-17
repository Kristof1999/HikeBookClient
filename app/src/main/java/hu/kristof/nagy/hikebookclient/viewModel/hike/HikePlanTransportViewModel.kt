package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.view.hike.TransportType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class HikePlanTransportViewModel @Inject constructor(
    private val userRouteRepository: UserRouteRepository
) : ViewModel() {
    var transportType = TransportType.BICYCLE

    var startPoint: GeoPoint = Constants.START_POINT
    var endPoint: GeoPoint = Constants.START_POINT

    private var _startPointChanged = MutableLiveData(false)
    val startPointChanged: LiveData<Boolean>
        get() = _startPointChanged

    private var _endPointChanged = MutableLiveData(false)
    val endPointChanged: LiveData<Boolean>
        get() = _endPointChanged

    private var _switchOffStart = MutableLiveData(false)
    val switchOffStart: LiveData<Boolean>
        get() = _switchOffStart

    private var _switchOffEnd = MutableLiveData(false)
    val switchOffEnd: LiveData<Boolean>
        get() = _switchOffEnd

    /**
     * Indicates if we want to put the start point on the map.
     */
    private var isStart = false
    /**
     * Indicates if we want to put the destination point on the map.
     */
    private var isEnd = false

    private var _route = MutableLiveData<Result<Route>>()
    val route: LiveData<Result<Route>>
        get() = _route

    fun loadRoute(routeName: String) {
        viewModelScope.launch {
            userRouteRepository.loadUserRouteOfLoggedInUser(routeName).collect {
                _route.value = it
            }
        }
    }

    fun setStartTo(value: Boolean) {
        if (value && isEnd) {
            isStart = value
            _switchOffEnd.value = !_switchOffEnd.value!!
        } else {
            isStart = value
        }
    }

    fun setEndTo(value: Boolean) {
        if (value && isStart) {
            isEnd = value
            _switchOffStart.value = !_switchOffStart.value!!
        } else {
            isEnd = value
        }
    }

    fun onSingleTap(p: GeoPoint) {
        if (isStart) {
            startPoint = p
            _startPointChanged.value = !_startPointChanged.value!!
        }

        if(isEnd) {
            endPoint = p
            _endPointChanged.value = !_endPointChanged.value!!
        }
    }
}