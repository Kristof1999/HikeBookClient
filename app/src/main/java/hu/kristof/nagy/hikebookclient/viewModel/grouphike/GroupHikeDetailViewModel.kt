package hu.kristof.nagy.hikebookclient.viewModel.grouphike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupHikeDetailViewModel @Inject constructor(
    private val groupHikeRepository: GroupHikeRepository
) : ViewModel() {
    private var _route = MutableLiveData<Route>()
    val route: LiveData<Route>
        get() = _route

    private var _participants = MutableLiveData<List<String>>()
    val participants: LiveData<List<String>>
        get() = _participants

    private var _generalConnectRes = MutableLiveData<Boolean>()
    val generalConnectRes: LiveData<Boolean>
        get() = _generalConnectRes

    fun loadRoute(groupHikeName: String) {
        viewModelScope.launch {
            _route.value = groupHikeRepository.loadRoute(groupHikeName)
        }
    }

    fun listParticipants(groupHikeName: String) {
        viewModelScope.launch {
            _participants.value = groupHikeRepository.listParticipants(groupHikeName)
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