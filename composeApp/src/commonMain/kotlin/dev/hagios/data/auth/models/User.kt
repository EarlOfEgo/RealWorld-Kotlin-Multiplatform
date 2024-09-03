package dev.hagios.data.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val bio: String?,
    val email: String,
    val id: Int?,
    val image: String?,
    val token: String,
    val username: String
)