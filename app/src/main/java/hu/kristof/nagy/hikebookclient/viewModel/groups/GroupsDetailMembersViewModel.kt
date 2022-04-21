package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to list the members of the given group.
 */
@HiltViewModel
class GroupsDetailMembersViewModel @Inject constructor(
    private val service: Service
    ) : ViewModel() {
    private var _members = MutableLiveData<ResponseResult<List<String>>>()
    val members: LiveData<ResponseResult<List<String>>>
        get() = _members

    fun listMembers(groupName: String) {
        viewModelScope.launch {
            _members.value = service.listMembers(groupName)
        }
    }
}