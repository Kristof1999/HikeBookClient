package hu.kristof.nagy.hikebookclient.viewModel

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var name: String = ""
    var pswd: String = ""

    fun onLogin(): String {
        return "$name $pswd"
    }
}