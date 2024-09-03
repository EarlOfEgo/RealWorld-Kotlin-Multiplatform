package dev.hagios.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.hagios.data.auth.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainScreenModel(
    authRepository: AuthRepository
) : ScreenModel {
    val isLoggedIn = authRepository.userLoggedIn.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
}