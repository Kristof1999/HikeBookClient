package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.view.hike.TransportType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

/**
 * A ViewModel that helps to load the route chosen for hiking,
 * and helps with setting the start and end points of
 * the route to the hike route.
 *
 * Ensures that we can't set the start and end points at the same time.
 */
@HiltViewModel
class HikePlanTransportViewModel @Inject constructor(
    private val userRouteRepository: IUserRouteRepository
) : ViewModel() {
    var transportType = TransportType.BICYCLE

    var startPoint: GeoPoint = Constants.START_POINT
    var endPoint: GeoPoint = Constants.START_POINT

    private val _startPointChanged = MutableLiveData(false)
    val startPointChanged: LiveData<Boolean>
        get() = _startPointChanged

    private val _endPointChanged = MutableLiveData(false)
    val endPointChanged: LiveData<Boolean>
        get() = _endPointChanged

    private val _setStartNext = MutableLiveData(false)
    val setStartNext: LiveData<Boolean>
        get() = _setStartNext

    private val _setEndNext = MutableLiveData(false)
    val setEndNext: LiveData<Boolean>
        get() = _setEndNext

    private val _route = MutableLiveData<ServerResponseResult<Route>>()
    val route: LiveData<ServerResponseResult<Route>>
        get() = _route

    fun loadRoute(routeName: String) {
        viewModelScope.launch {
            userRouteRepository.loadUserRouteOfLoggedInUser(routeName).collect {
                _route.value = it as ServerResponseResult<Route>
            }
        }
    }

    fun setStartTo(value: java.lang.Boolean) {
        _setStartNext.value = value as kotlin.Boolean
        if (shoudSetBoth()) {
            _setEndNext.value = false
        }
    }

    fun setEndTo(value: java.lang.Boolean) {
        _setEndNext.value = value as kotlin.Boolean
        if (shoudSetBoth()) {
            _setStartNext.value = false
        }
    }

    fun onSingleTap(p: GeoPoint) {
        if (_setStartNext.value!!) {
            startPoint = p
            notifyViewWith(_startPointChanged)
        }

        if(_setEndNext.value!!) {
            endPoint = p
            notifyViewWith(_endPointChanged)
        }
    }

    private fun shoudSetBoth(): Boolean = _setEndNext.value!! && _setStartNext.value!!

    private fun notifyViewWith(liveData: MutableLiveData<Boolean>) {
        liveData.value = !liveData.value!!
    }
}