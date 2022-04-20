package hu.kristof.nagy.hikebookclient.model

import java.security.MessageDigest

data class User(val name: String, var password: String) {
    val avgSpeed: Double = 0.0

    init {
        checkName(name)
        checkPassword()
    }

    fun encryptPassword() {
        password = MessageDigest.getInstance("MD5").digest(
            password.toByteArray()
        ).joinToString(separator = "")
    }

    private fun checkPassword() {
        if (password.isEmpty()) {
            throw IllegalArgumentException("A jelszó nem lehet üres.")
        }
        if (password.length < 6) {
            throw IllegalArgumentException("A jelszónak legalább 6 karakterből kell állnia.")
        }
    }

    companion object {
        fun checkName(name: String) {
            if (name.isEmpty()) {
                throw IllegalArgumentException("A név nem lehet üres.")
            }
            if (name.contains("/")) {
                throw IllegalArgumentException("A név nem tartalmazhat / jelet.")
            }
        }
    }
}