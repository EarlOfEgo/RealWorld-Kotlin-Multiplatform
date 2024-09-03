package dev.hagios.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class Register(
    val username: String,
    val email: String,
    val password: String
)
