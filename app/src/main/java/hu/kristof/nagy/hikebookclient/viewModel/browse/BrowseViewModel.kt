package hu.kristof.nagy.hikebookclient.viewModel.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.repository.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to list all the user routes.
 */
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val userRouteRepository: IUserRouteRepository
    ) : ViewModel() {
    private val _items = MutableLiveData<ServerResponseResult<List<BrowseListItem>>>()
    val items: LiveData<ServerResponseResult<List<BrowseListItem>>>
        get() = _items

    fun listRoutes() {
        viewModelScope.launch {
            userRouteRepository.listUserRoutesForLoggedInUser().collect {
                _items.value = it
            }
        }
    }
}