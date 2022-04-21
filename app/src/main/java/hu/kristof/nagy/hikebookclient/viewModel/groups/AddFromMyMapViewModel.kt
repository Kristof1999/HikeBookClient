package hu.kristof.nagy.hikebookclient.viewModel.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel that helps to list the user's
 * route names.
 */
@HiltViewModel
class AddFromMyMapViewModel @Inject constructor(
    private val userRepository: IUserRouteRepository
    ) : ViewModel() {
    private var _routes = MutableLiveData<ResponseResult<List<UserRoute>>>()
    val routes: LiveData<ResponseResult<List<UserRoute>>>
        get() = _routes

    fun loadRoutesForLoggedInUser() {
        // TODO: only load names of routes
        viewModelScope.launch {
            userRepository.loadUserRoutesOfLoggedInUser()
                .collect{ routes ->
                    _routes.value = routes
                }
        }
    }
}