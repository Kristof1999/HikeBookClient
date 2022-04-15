package hu.kristof.nagy.hikebookclient.viewModel.mymap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMapViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository
    ) : ViewModel() {

    private var _routes = MutableLiveData<Result<List<UserRoute>>>()
    /**
     * List of the logged in user's routes.
     */
    val routes: LiveData<Result<List<UserRoute>>>
        get() = _routes

    fun loadRoutesForLoggedInUser() {
        viewModelScope.launch {
            userRepository.loadUserRoutes()
                .collect{ routes ->
                    _routes.value = routes
            }
        }
    }
}