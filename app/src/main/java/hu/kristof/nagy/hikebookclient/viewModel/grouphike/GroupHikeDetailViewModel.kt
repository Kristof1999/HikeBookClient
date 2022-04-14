package hu.kristof.nagy.hikebookclient.viewModel.grouphike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupHikeDetailViewModel @Inject constructor(
    private val groupHikeRepository: GroupHikeRepository
) : ViewModel() {
    private var _route = MutableLiveData<Route>()
    val route: LiveData<Route>
        get() = _route

    fun loadRoute(groupHikeName: String) {
        viewModelScope.launch {
            _route.value = groupHikeRepository.loadRoute(groupHikeName)
        }
    }
}