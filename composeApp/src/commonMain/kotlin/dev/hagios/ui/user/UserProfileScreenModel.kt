package dev.hagios.ui.user

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.hagios.data.auth.AuthRepository
import dev.hagios.data.auth.models.User
import dev.hagios.data.user.UserRepository
import dev.hagios.ui.user.UserProfileUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UserProfileScreenModel(
    private val userRepository: UserRepository,
) : ScreenModel {
    private val _fetchTrigger: MutableStateFlow<Any> = MutableStateFlow(Any())

    fun retryFetching() {
        _fetchTrigger.value = Any()
    }

    val profile = _fetchTrigger.flatMapLatest {
        userRepository.getUserProfile()
    }
        .map<User, UserProfileUiState>(::Success)
        .catch { emit(UserProfileUiState.Error(it)) }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(), UserProfileUiState.Loading)

}


sealed interface UserProfileUiState {
    data object Loading : UserProfileUiState
    data class Error(val throwable: Throwable) : UserProfileUiState
    data class Success(val data: User) : UserProfileUiState
}