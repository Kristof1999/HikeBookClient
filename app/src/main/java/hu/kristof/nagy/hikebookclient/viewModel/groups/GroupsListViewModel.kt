package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupsRepository
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps the user to
 * join/leave a given group, and
 * helps with listing the groups.
 */
@HiltViewModel
class GroupsListViewModel @Inject constructor(
    private val repository: GroupsRepository
) : ViewModel() {
    private val _groups = MutableLiveData<ServerResponseResult<List<String>>>()
    val groups: LiveData<ServerResponseResult<List<String>>>
        get() = _groups

    private val _generalConnectRes = MutableLiveData<ServerResponseResult<Boolean>>()
    val generalConnectRes: LiveData<ServerResponseResult<Boolean>>
        get() = _generalConnectRes

    var generalConnectFinished = true

    fun generalConnect(groupName: String, isConnectedPage: Boolean) {
        generalConnectFinished = false
        viewModelScope.launch {
            repository.generalConnectForLoggedInUser(groupName, isConnectedPage).collect { res ->
                _generalConnectRes.value = res
            }
        }
    }

    fun listGroups(isConnectedPage: Boolean) {
        viewModelScope.launch {
            repository.listGroupsForLoggedInUser(isConnectedPage).collect { groupsRes ->
                _groups.value = groupsRes
            }
        }
    }
}