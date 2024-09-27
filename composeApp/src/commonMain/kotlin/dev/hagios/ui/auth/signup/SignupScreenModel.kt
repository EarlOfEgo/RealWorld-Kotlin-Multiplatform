package dev.hagios.ui.auth.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.screenModelScope
import dev.hagios.data.auth.AuthRepository
import dev.hagios.data.auth.EmailAddressTakenException
import dev.hagios.data.auth.models.User
import dev.hagios.ui.auth.AuthBaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SignupScreenModel(
    private val authRepository: AuthRepository
) : AuthBaseScreenModel() {

    var username by mutableStateOf("")
        private set

    val allowSignup: StateFlow<Boolean> =
        combine(
            snapshotFlow { email },
            snapshotFlow { username },
            snapshotFlow { password }) { email, username, password ->
            email.isNotBlank() && username.isNotBlank() && password.isNotBlank()
        }
            .stateIn(
                scope = screenModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    private val _registerRequest = MutableStateFlow<User?>(null)

    val registerRequest = _registerRequest.asStateFlow()

    fun updateName(input: String) {
        username = input
    }

    fun registerUser() {
        screenModelScope.launch {
            loading = true
            try {
                val user = authRepository.registerUser(username, email, password)
                _registerRequest.emit(user)
            } catch (_: EmailAddressTakenException) {
                emailError = true
            } catch (e: Exception) {
                println(e)
            }
            loading = false
        }
    }
}