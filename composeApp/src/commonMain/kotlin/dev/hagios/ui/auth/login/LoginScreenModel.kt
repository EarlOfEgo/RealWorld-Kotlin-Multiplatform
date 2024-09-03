package dev.hagios.ui.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.hagios.data.auth.AuthRepository
import dev.hagios.data.auth.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val authRepository: AuthRepository
): ScreenModel {
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    val allowLogin: StateFlow<Boolean> =
        combine(
            snapshotFlow { email },
            snapshotFlow { password }) { email, password ->
            email.isNotBlank() && password.isNotBlank()
        }
            .stateIn(
                scope = screenModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    private val _loginRequest = MutableStateFlow<User?>(null)

    val loginRequest = _loginRequest.asStateFlow()

    fun updateEmail(input: String) {
        email = input
    }

    fun updatePassword(input: String) {
        password = input
    }

    fun loginUser() {
        screenModelScope.launch {
            try {
                val user = authRepository.loginUser(email, password)
                _loginRequest.emit(user)
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}