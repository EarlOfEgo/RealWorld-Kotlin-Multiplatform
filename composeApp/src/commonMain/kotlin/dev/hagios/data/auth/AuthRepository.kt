package dev.hagios.data.auth

import dev.hagios.data.auth.models.AuthRequest
import dev.hagios.data.auth.models.Login
import dev.hagios.data.auth.models.Register
import dev.hagios.data.auth.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class AuthRepository(
    private val authApi: AuthApi,
    private val authDataStore: AuthDataStore
) {

    suspend fun registerUser(
        username: String,
        email: String,
        password: String
    ): User {
        val response = authApi.signupUser(
            AuthRequest(Register(username = username, email = email, password = password))
        )
        authDataStore.storeToken(response.user.token)
        return response.user
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): User {
        val response = authApi.loginUser(
            AuthRequest(Login(email = email, password = password))
        )
        authDataStore.storeToken(response.user.token)
        return response.user
    }

    suspend fun userIsLoggedIn(): Boolean = authDataStore.getToken().firstOrNull() != null

    val userLoggedIn: Flow<Boolean> = authDataStore.userLoggedIn

    val accessToken = runBlocking { authDataStore.getToken().firstOrNull() }
}