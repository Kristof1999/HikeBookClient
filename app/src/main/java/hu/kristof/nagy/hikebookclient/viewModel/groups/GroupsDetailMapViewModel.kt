package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupRouteRepository
import hu.kristof.nagy.hikebookclient.model.Route
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsDetailMapViewModel @Inject constructor(
    private val groupRepository: GroupRouteRepository
    ) : ViewModel() {
    private var _routes = MutableLiveData<Result<List<Route>>>()
    val routes: LiveData<Result<List<Route>>>
        get() = _routes

    fun loadRoutesOfGroup(groupName: String) {
        viewModelScope.launch {
            _routes.value = groupRepository.loadRoutes(groupName)
        }
    }
}