package hu.kristof.nagy.hikebookclient.viewModel

import hu.kristof.nagy.hikebookclient.model.UserAuth

object AuthChecker {
    fun check(user: UserAuth) {
        if (user.name.length == 0 || user.password.length == 0) {
            throw IllegalArgumentException("A név és a jelszó mezők nem lehetnek üresek.")
        }
        if (user.password.length < 6) {
            throw java.lang.IllegalArgumentException("A jelszónak legalább 6 karakterből kell állnia.")
        }
    }
}