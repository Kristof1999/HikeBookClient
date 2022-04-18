package hu.kristof.nagy.hikebookclient.model

import java.security.MessageDigest

data class User(val name: String, var password: String) {
    val avgSpeed: Double = 0.0

    init {
        checkName()
        checkPassword()
    }

    fun encryptPassword() {
        password = MessageDigest.getInstance("MD5").digest(
            password.toByteArray()
        ).joinToString(separator = "")
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