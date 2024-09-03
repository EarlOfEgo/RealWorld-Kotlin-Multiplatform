package dev.hagios.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse<T>(val user: T)
