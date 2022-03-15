package hu.kristof.nagy.hikebookclient.viewModel.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.network.Service
import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val service: Service
    ) : ViewModel() {

    private var _routes = MutableLiveData<List<BrowseListItem>>()
    val routes: LiveData<List<BrowseListItem>>
        get() = _routes

    fun listRoutes() {
        viewModelScope.launch {
            _routes.value = service.listRoutes()
        }
    }


}