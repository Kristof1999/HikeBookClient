package hu.kristof.nagy.hikebookclient.viewModel.authentication

import hu.kristof.nagy.hikebookclient.model.UserAuth

object AuthChecker {
    /**
     * Performs checks on the given user: checks if
     * both the name and password is not empty, etc.
     * @param user: the user to be checked
     * @throws IllegalArgumentException: if the user fails the checks
     */
    fun check(user: UserAuth) {
        if (user.name.isEmpty() || user.password.isEmpty()) {
            throw IllegalArgumentException("A név és a jelszó mezők nem lehetnek üresek.")
        }
        if (user.password.length < 6) {
            throw IllegalArgumentException("A jelszónak legalább 6 karakterből kell állnia.")
        }
        if (user.name.contains("/")) {
            throw IllegalArgumentException("A név nem tartalmazhat / jelet.")
        }
    }
}