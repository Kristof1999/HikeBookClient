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

/**
 * A ViewModel that helps to create groups.
 */
@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val repository: GroupsRepository
    ): ViewModel() {
    private var _createRes = MutableLiveData<Result<Boolean>>()
    val createRes: LiveData<Result<Boolean>>
        get() = _createRes

    var createFinished = true

    /**
     * Checks if the given group name is not empty,
     * and does not contain the / symbol.
     * If the name is not ok, then it throws the appropriate exceptions.
     * If the name is ok, then it calls the data layer to create the group,
     * and notifies the view layer of the result.
     * @throws IllegalArgumentException if the group name is not ok
     */
    fun createGroup(name: String) {
        check(name)

        createFinished = false
        viewModelScope.launch {
            repository.createGroupForLoggedInUser(name).collect { res ->
                _createRes.value = res
            }
        }
    }

    private fun check(name: String) {
        if (name.isEmpty())
            throw IllegalArgumentException("A név nem lehet üres.")
        if (name.contains("/"))
            throw IllegalArgumentException("A név nem tartalmazhat / jelet.")
    }
}