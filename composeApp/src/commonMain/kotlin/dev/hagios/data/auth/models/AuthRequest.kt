package dev.hagios.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest<T>(val user: T)
