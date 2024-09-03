package dev.hagios.data.user

import dev.hagios.data.auth.models.AuthResponse
import dev.hagios.data.auth.models.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

interface UserApi {
    suspend fun getUserProfile(): AuthResponse<User>
}

class KtorUserApi(private val client: HttpClient) : UserApi {

    override suspend fun getUserProfile(): AuthResponse<User> {
        return client.get() {
            url { path("user") }
            contentType(ContentType.Application.Json)
        }.body()
    }
}