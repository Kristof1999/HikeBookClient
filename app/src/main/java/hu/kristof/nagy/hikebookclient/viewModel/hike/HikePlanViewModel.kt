package hu.kristof.nagy.hikebookclient.viewModel.hike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.BuildConfig
import hu.kristof.nagy.hikebookclient.data.network.WeatherService
import hu.kristof.nagy.hikebookclient.util.Constants
import hu.kristof.nagy.hikebookclient.view.hike.TransportType
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class HikePlanViewModel @Inject constructor(
    private val service: WeatherService
    ) : ViewModel() {
    var transportType = TransportType.NOTHING

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

    private var _forecastRes = MutableLiveData<String>()
    val forecastRes: LiveData<String>
        get() = _forecastRes

    /**
     * Indicates if we want to put the start point on the map.
     */
    private var isStart = false
    /**
     * Indicates if we want to put the destination point on the map.
     */
    private var isEnd = false

    fun setStartTo(value: Boolean) {
        if (value && isEnd) {
            isStart = value
            _switchOffEnd.value = !_switchOffEnd.value!!
        } else {
            isStart = value
        }
    }

    fun setDestinationTo(value: Boolean) {
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

    fun forecast() {
        viewModelScope.launch {
            _forecastRes.value = service.forecast(
                startPoint.latitude, startPoint.longitude,
                Constants.METRIC_UNIT, Constants.FORECAST_CNT,
                BuildConfig.WEATHER_API_KEY
            ).toString()
        }
    }
}