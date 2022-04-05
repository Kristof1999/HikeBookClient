package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.network.handleRequest
import hu.kristof.nagy.hikebookclient.di.Service
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val service: Service
    ): ViewModel() {
    private var _createRes = MutableLiveData<Result<Boolean>>()
    val createRes: LiveData<Result<Boolean>>
        get() = _createRes

    fun createGroup(name: String) {
        check(name)

        viewModelScope.launch {
            _createRes.value = handleRequest {
                service.createGroup(name)
            }!!
        }
    }

    fun check(name: String) {
        if (name.isEmpty())
            throw IllegalArgumentException("A név nem lehet üres.")
        if (name.contains("/"))
            throw IllegalArgumentException("A név nem tartalmazhat / jelet.")
    }
}