package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.routes.Route
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFromMyMapViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository
    ) : ViewModel() {
    private var _routes = MutableLiveData<Result<List<Route>>>()
    val routes: LiveData<Result<List<Route>>>
        get() = _routes

    fun loadRoutesForLoggedInUser() {
        // TODO: only load names of routes
        viewModelScope.launch {
            userRepository.loadUserRoutes()
                .collect{ routes ->
                    _routes.value = routes
                }
        }
    }
}