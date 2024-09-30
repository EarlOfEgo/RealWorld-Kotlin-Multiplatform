package dev.hagios.data.user

import dev.hagios.data.auth.models.User
import dev.hagios.data.user.models.UpdateProfileRequest
import kotlinx.coroutines.flow.flow

class UserRepository(
    private val userApi: UserApi
) {
    fun getUserProfile() = flow {
        emit(userApi.getUserProfile().user)
    }

    suspend fun updateProfile(
        username: String?,
        bio: String?,
        email: String?,
        image: String?,
        password: String?
    ): User {
        val updateProfileRequest = UpdateProfileRequest(
            user = UpdateProfileRequest.User(
                bio = bio,
                email = email,
                id = null,
                image = image,
                username = username,
                password = password
            )
        )
        return userApi.updateProfile(updateProfileRequest).user
    }
}

