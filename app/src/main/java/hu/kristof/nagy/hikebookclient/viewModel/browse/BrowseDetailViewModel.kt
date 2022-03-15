package hu.kristof.nagy.hikebookclient.viewModel.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.Point
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseDetailViewModel @Inject constructor(
    private val service: Service
    ) : ViewModel() {
    private var _points = MutableLiveData<List<Point>>()
    val points: LiveData<List<Point>>
        get() = _points

    fun loadPoints(userName: String, routeName: String) {
        viewModelScope.launch {
            _points.value = service.loadPoints(userName, routeName)
        }
    }
}