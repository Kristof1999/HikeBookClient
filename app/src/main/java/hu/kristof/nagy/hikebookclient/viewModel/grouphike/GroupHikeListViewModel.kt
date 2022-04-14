package hu.kristof.nagy.hikebookclient.viewModel.grouphike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.GroupHikeListHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupHikeListViewModel @Inject constructor(
    private val groupHikeRepository: GroupHikeRepository
) : ViewModel() {
    private var _groupHikes = MutableLiveData<List<GroupHikeListHelper>>()
    val groupHikes: LiveData<List<GroupHikeListHelper>>
        get() = _groupHikes

    private var _generalConnectRes = MutableLiveData<Boolean>()
    val generalConnectRes: LiveData<Boolean>
        get() = _generalConnectRes

    fun listGroupHikes(isConnectedPage: Boolean) {
        viewModelScope.launch {
            groupHikeRepository.listGroupHikes(isConnectedPage).collect {
                _groupHikes.value = it
            }
        }
    }

    fun generalConnect(groupHikeName: String, isConnectedPage: Boolean, dateTime: DateTime) {
        viewModelScope.launch {
            groupHikeRepository.generalConnect(groupHikeName, isConnectedPage, dateTime).collect {
                _generalConnectRes.value = it
            }
        }
    }
}