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

/**
 * A ViewModel that helps to load the route chosen for hiking,
 * and helps with setting the start and end points of
 * the route to the hike route.
 *
 * Ensures that we can't set the start and end points at the same time.
 */
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

    private var _setStartNext = MutableLiveData(false)
    val setStartNext: LiveData<Boolean>
        get() = _setStartNext

    private var _setEndNext = MutableLiveData(false)
    val setEndNext: LiveData<Boolean>
        get() = _setEndNext

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
        _setStartNext.value = value
        if (shoudSetBoth()) {
            _setEndNext.value = false
        }
    }

    fun setEndTo(value: Boolean) {
        _setEndNext.value = value
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