package dev.hagios.ui.profile.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.hagios.data.auth.models.User
import dev.hagios.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditProfileScreenModel(
    private val userRepository: UserRepository,
) : ScreenModel {
    val allowUpdate: StateFlow<Boolean> = combine(
        snapshotFlow { username },
        snapshotFlow { bio },
        snapshotFlow { email },
        snapshotFlow { pictureUrl },
        snapshotFlow { password }
    ) { username, bio, email, pictureUrl, password ->
        username.isNotBlank() || bio.isNotBlank() || email.isNotBlank() || pictureUrl.isNotBlank() || password.isNotBlank()
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    var username by mutableStateOf("")
        private set

    var bio by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var pictureUrl by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    fun updateUsername(input: String) {
        username = input
    }

    fun updateBio(input: String) {
        bio = input
    }

    fun updateEmail(input: String) {
        email = input
    }

    fun updatePictureUrl(input: String) {
        pictureUrl = input
    }

    fun updatePassword(input: String) {
        password = input
    }

    init {
        screenModelScope.launch {

            userRepository.getUserProfile().collect {
                it.image?.let { image -> pictureUrl = image }
                username = it.username
                email = it.email
                it.bio?.let { _ -> bio = it.bio }
            }
        }
    }

    private val _updateUserRequest = MutableStateFlow<User?>(null)
    val updateUserRequest = _updateUserRequest.asStateFlow()

    fun update() {
        screenModelScope.launch {
            try {
                val newUser = userRepository.updateProfile(
                    username = username.ifBlank { null },
                    bio = bio.ifBlank { null },
                    email = email.ifBlank { null },
                    image = pictureUrl.ifBlank { null },
                    password = password.ifBlank { null }
                )
                _updateUserRequest.emit(newUser)
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}
