package dev.hagios.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel

abstract class AuthBaseScreenModel : ScreenModel {
    var email by mutableStateOf("")
        protected set

    var emailError by mutableStateOf(false)
        protected set

    var loading by mutableStateOf(false)
        protected set

    var password by mutableStateOf("")
        protected set

    var passwordError by mutableStateOf(false)
        protected set

    fun updateEmail(input: String) {
        email = input
        emailError = false
    }

    fun updatePassword(input: String) {
        password = input
        passwordError = false
    }
}