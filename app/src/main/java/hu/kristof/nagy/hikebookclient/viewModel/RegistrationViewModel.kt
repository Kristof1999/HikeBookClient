package hu.kristof.nagy.hikebookclient.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.kristof.nagy.hikebookclient.model.UserAuth
import hu.kristof.nagy.hikebookclient.network.Api
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    var name: String = ""
    var pswd: String = ""

    private var _registrationRes = MutableLiveData<Boolean>()
    val registrationRes : LiveData<Boolean>
        get() = _registrationRes

    private val service = Api.retrofitService

    fun onRegister() {
        viewModelScope.launch{
            _registrationRes.value = service.register(UserAuth(name, pswd))
        }
    }
}