package hu.kristof.nagy.hikebookclient.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.kristof.nagy.hikebookclient.model.UserAuth
import hu.kristof.nagy.hikebookclient.network.Service
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private var _registrationRes = MutableLiveData<Boolean>()
    val registrationRes : LiveData<Boolean>
        get() = _registrationRes

    fun onRegister(user: UserAuth) {
        viewModelScope.launch{
            _registrationRes.value = service.register(user)
        }
    }
}