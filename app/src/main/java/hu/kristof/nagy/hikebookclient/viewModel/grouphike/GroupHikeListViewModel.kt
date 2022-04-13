package hu.kristof.nagy.hikebookclient.viewModel.grouphike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kristof.nagy.hikebookclient.data.GroupHikeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupHikeListViewModel @Inject constructor(
    private val groupHikeRepository: GroupHikeRepository
) : ViewModel() {
    private var _groupHikes = MutableLiveData<List<String>>()
    val groupHikes: LiveData<List<String>>
        get() = _groupHikes

    fun listGroupHikes(isConnectedPage: Boolean) {
        viewModelScope.launch {
            groupHikeRepository.listGroupHikes(isConnectedPage).collect {
                _groupHikes.value = it
            }
        }
    }
}