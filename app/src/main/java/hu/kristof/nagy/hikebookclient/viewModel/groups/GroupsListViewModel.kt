package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsListViewModel @Inject constructor(
    private val repository: GroupsRepository
) : ViewModel() {
    private var _groups = MutableLiveData<List<String>>()
    val groups: LiveData<List<String>>
        get() = _groups

    fun listGroups(isConnectedPage: Boolean) {
        viewModelScope.launch {
            repository.listGroups(isConnectedPage).collect { groupsRes ->
                _groups.value = groupsRes
            }
        }
    }
}