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
class GroupsViewModel @Inject constructor(
    private val repository: GroupsRepository
    ): ViewModel() {
    private var _createRes = MutableLiveData<Result<Boolean>>()
    val createRes: LiveData<Result<Boolean>>
        get() = _createRes

    fun createGroup(name: String) {
        check(name)

        viewModelScope.launch {
            repository.createGroup(name).collect { res ->
                _createRes.value = res
            }
        }
    }

    fun check(name: String) {
        if (name.isEmpty())
            throw IllegalArgumentException("A név nem lehet üres.")
        if (name.contains("/"))
            throw IllegalArgumentException("A név nem tartalmazhat / jelet.")
    }
}