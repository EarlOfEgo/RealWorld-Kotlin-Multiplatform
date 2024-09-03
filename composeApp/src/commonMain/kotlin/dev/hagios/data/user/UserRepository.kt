package dev.hagios.data.user

import kotlinx.coroutines.flow.flow

class UserRepository(
    private val userApi: UserApi
) {
    fun getUserProfile() = flow {
        emit(userApi.getUserProfile().user)
    }
}