package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.repository.routes.IGroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.repository.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to add routes from the group's map
 * to the user's map, and helps with deleting group routes.
 */
@HiltViewModel
class GroupsDetailListViewModel @Inject constructor(
    private val groupRouteRepository: IGroupRouteRepository,
    private val userRouteRepository: IUserRouteRepository
) : ViewModel() {
    private val _addToMyMapRes = MutableLiveData<ResponseResult<Boolean>>()
    val addToMyMapRes: LiveData<ResponseResult<Boolean>>
        get() = _addToMyMapRes

    var addToMyMapFinished = true

    private val _deleteRes = MutableLiveData<ServerResponseResult<Boolean>>()
    val deleteRes: LiveData<ServerResponseResult<Boolean>>
        get() = _deleteRes

    var deleteFinished = true

    fun addToMyMap(route: Route) {
        addToMyMapFinished = false
        viewModelScope.launch {
            userRouteRepository.createUserRouteForLoggedInUser(
                route.routeName, route.points, route.description
            ).collect { res ->
                _addToMyMapRes.value = res
            }
        }
    }

    fun delete(groupName: String, routeName: String) {
        deleteFinished = false
        viewModelScope.launch {
            _deleteRes.value = groupRouteRepository.deleteGroupRoute(groupName, routeName)
        }
    }
}