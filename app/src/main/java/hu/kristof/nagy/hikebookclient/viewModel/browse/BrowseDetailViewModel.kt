package hu.kristof.nagy.hikebookclient.viewModel.browse

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseDetailViewModel @Inject constructor(
    private val service: Service,
    private val dataStore: DataStore<Preferences>
    ) : ViewModel() {
    private var _route = MutableLiveData<Result<Route>>()
    /**
     * Points of the chosen route.
     */
    val route: LiveData<Result<Route>>
        get() = _route

    private var _addRes = MutableLiveData<Result<Boolean>>()
    /**
     * The result of trying to add the route to the map of the logged in user.
     */
    val addRes: LiveData<Result<Boolean>>
        get() = _addRes

    fun loadDetails(userName: String, routeName: String) {
        viewModelScope.launch {
            _route.value = handleRequest {
                service.loadRoute(userName, routeName, RouteType.USER)
            }!!
        }
    }

    fun addToMyMap(routeName: String) {
        viewModelScope.launch {
            // TODO: move data store usage to a repository
            dataStore.data.map {
                it[Constants.DATA_STORE_USER_NAME]
            }.collect { userName ->
                if (_route.value != null && route.value!!.getOrNull() != null) {
                    _addRes.value = handleRequest {
                        service.createRoute(userName!!, routeName, _route.value!!.getOrNull()!!)
                    }!!
                } else {
                    throw IllegalStateException("Az útvonal még nem töltődött be.")
                }
            }
        }
    }
}