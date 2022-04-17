package hu.kristof.nagy.hikebookclient.model

import java.security.MessageDigest

data class UserAuth(
    val name: String, val password: String,
    private val isPasswordEncrypted: Boolean = false // TODO: change on server too
) {
    val avgSpeed: Double = 0.0

    init {
        checkName()
        if (!isPasswordEncrypted) {
            checkPassword()
        }
    }

    fun encryptPassword(): UserAuth {
        val encyptedPassword = MessageDigest.getInstance("MD5").digest(
            password.toByteArray()
        ).joinToString(separator = "")
        return UserAuth(name, encyptedPassword, true)
    }

    private fun checkName() {
        if (name.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("A név és a jelszó mezők nem lehetnek üresek.")
        }
        if (name.contains("/")) {
            throw IllegalArgumentException("A név nem tartalmazhat / jelet.")
        }
    }

    private fun checkPassword() {
        if (password.length < 6) {
            throw IllegalArgumentException("A jelszónak legalább 6 karakterből kell állnia.")
        }
    }
}