package dev.hagios.data.user.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(val user: User) {
    @Serializable
    data class User(
        val bio: String?,
        val email: String?,
        val id: Int?,
        val image: String?,
        val username: String?,
        val password: String?
    )
}