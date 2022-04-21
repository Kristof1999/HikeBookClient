package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupsRepository
import hu.kristof.nagy.hikebookclient.model.ResponseResult
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
    private var _groups = MutableLiveData<ResponseResult<List<String>>>()
    val groups: LiveData<ResponseResult<List<String>>>
        get() = _groups

    private var _generalConnectRes = MutableLiveData<ResponseResult<Boolean>>()
    val generalConnectRes: LiveData<ResponseResult<Boolean>>
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