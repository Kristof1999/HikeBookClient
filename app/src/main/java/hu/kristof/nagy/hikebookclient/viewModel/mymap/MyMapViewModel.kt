package hu.kristof.nagy.hikebookclient.viewModel.mymap

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.util.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMapViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val service: Service
    ) : ViewModel() {

    private var _routes = MutableLiveData<List<Route>>()
    /**
     * List of the logged in user's routes.
     */
    val routes: LiveData<List<Route>>
        get() = _routes

    /**
     * Variable indicating that the deletion has finished.
     */
    var deleteFinished = true

    private var _deleteRes = MutableLiveData<Boolean>()
    /**
     * Result of deletion attempt.
     */
    val deleteRes: LiveData<Boolean>
        get() = _deleteRes

    fun loadRoutesForLoggedInUser() {
        viewModelScope.launch {
            dataStore.data.map {
                it[Constants.DATA_STORE_USER_NAME]
            }.collect { userName ->
                _routes.value = service.loadRoutesForUser(userName!!)
            }
        }
    }

    fun deleteRoute(routeName: String) {
        deleteFinished = false
        viewModelScope.launch {
            dataStore.data.map {
                it[Constants.DATA_STORE_USER_NAME]
            }.collect { userName ->
                _deleteRes.value = service.deleteRoute(userName!!, routeName)
            }
        }
    }

    fun getRoute(routeName: String): Route {
        return _routes.value!!.filter { route ->
            route.routeName == routeName
        }[0]
    }
}