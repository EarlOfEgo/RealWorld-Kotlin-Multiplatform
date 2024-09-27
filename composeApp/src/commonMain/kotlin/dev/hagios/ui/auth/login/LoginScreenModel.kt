package dev.hagios.ui.auth.login

import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.screenModelScope
import dev.hagios.data.auth.AuthRepository
import dev.hagios.data.auth.InvalidPasswordException
import dev.hagios.data.auth.UnknownEmailException
import dev.hagios.data.auth.models.User
import dev.hagios.ui.auth.AuthBaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val authRepository: AuthRepository
) : AuthBaseScreenModel() {

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

    fun loginUser() {
        screenModelScope.launch {
            loading = true
            try {
                val user = authRepository.loginUser(email, password)
                _loginRequest.emit(user)
            } catch (_: InvalidPasswordException) {
                passwordError = true
            } catch (_: UnknownEmailException) {
                emailError = true
            } catch (e: Exception) {
                println(e)
                passwordError = true
                emailError = true
            }
            loading = false
        }
    }
}