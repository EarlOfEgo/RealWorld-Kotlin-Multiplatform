package dev.hagios.data.user

import dev.hagios.data.auth.getBodyIfSuccess
import dev.hagios.data.auth.models.AuthResponse
import dev.hagios.data.auth.models.User
import dev.hagios.data.user.models.UpdateProfileRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

interface UserApi {
    suspend fun getUserProfile(): AuthResponse<User>
    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest): AuthResponse<User>
}

class KtorUserApi(private val client: HttpClient) : UserApi {

    override suspend fun getUserProfile(): AuthResponse<User> {
        return client.get {
            url { path("user") }
            contentType(ContentType.Application.Json)
        }.getBodyIfSuccess<AuthResponse<User>>()
    }

    override suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest): AuthResponse<User> {
        return client.put {
            url { path("user") }
            setBody(updateProfileRequest)
            contentType(ContentType.Application.Json)
        }.getBodyIfSuccess<AuthResponse<User>>()
    }
}