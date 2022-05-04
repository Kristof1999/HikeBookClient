package hu.kristof.nagy.hikebookclient.viewModel.groups

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.repository.GroupsRepository
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.util.handleOffline
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps the user to
 * join/leave a given group.
 */
@HiltViewModel
class GroupsDetailViewModel @Inject constructor(
    private val repository: GroupsRepository
)  : ViewModel() {
    private val _generalConnectRes = MutableLiveData<ResponseResult<Boolean>>()
    val generalConnectRes: LiveData<ResponseResult<Boolean>>
        get() = _generalConnectRes

    fun generalConnect(
        groupName: java.lang.String,
        isConnectedPage: java.lang.Boolean,
        context: Context
    ) {
        viewModelScope.launch {
            handleOffline(_generalConnectRes, context) {
                repository
                    .generalConnectForLoggedInUser(
                        groupName as kotlin.String,
                        isConnectedPage as kotlin.Boolean
                    )
                    .collect { res ->
                        _generalConnectRes.value = res
                    }
            }
        }
    }
}