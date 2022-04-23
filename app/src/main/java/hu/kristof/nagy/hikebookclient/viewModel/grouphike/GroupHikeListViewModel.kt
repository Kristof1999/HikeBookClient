package hu.kristof.nagy.hikebookclient.viewModel.grouphike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.GroupHikeListHelper
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to list group hikes,
 * and join/leave a group hike.
 */
@HiltViewModel
class GroupHikeListViewModel @Inject constructor(
    private val groupHikeRepository: GroupHikeRepository
) : ViewModel() {
    private val _groupHikes = MutableLiveData<ResponseResult<List<GroupHikeListHelper>>>()
    val groupHikes: LiveData<ResponseResult<List<GroupHikeListHelper>>>
        get() = _groupHikes

    private val _generalConnectRes = MutableLiveData<ResponseResult<Boolean>>()
    val generalConnectRes: LiveData<ResponseResult<Boolean>>
        get() = _generalConnectRes

    var generalConnectFinished = true

    fun listGroupHikes(isConnectedPage: Boolean) {
        viewModelScope.launch {
            groupHikeRepository
                .listGroupHikesForLoggedInUser(isConnectedPage)
                .collect {
                _groupHikes.value = it
            }
        }
    }

    fun generalConnect(groupHikeName: String, isConnectedPage: Boolean, dateTime: DateTime) {
        generalConnectFinished = false
        viewModelScope.launch {
            groupHikeRepository
                .generalConnectForLoggedInUser(groupHikeName, isConnectedPage, dateTime)
                .collect {
                _generalConnectRes.value = it
            }
        }
    }
}