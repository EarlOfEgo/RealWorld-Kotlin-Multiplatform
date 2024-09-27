package dev.hagios.data.auth

import dev.hagios.data.auth.models.AuthRequest
import dev.hagios.data.auth.models.AuthResponse
import dev.hagios.data.auth.models.Login
import dev.hagios.data.auth.models.Register
import dev.hagios.data.auth.models.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

interface AuthApi {
    suspend fun signupUser(
        userCredentials: AuthRequest<Register>
    ): AuthResponse<User>

    suspend fun loginUser(
        userCredentials: AuthRequest<Login>
    ): AuthResponse<User>
}

class KtorAuthApi(private val client: HttpClient) : AuthApi {

    override suspend fun signupUser(userCredentials: AuthRequest<Register>): AuthResponse<User> {
        return client.post {
            url { path("users") }
            contentType(ContentType.Application.Json)
            setBody(userCredentials)
        }.getBodyIfSuccess()
    }

    override suspend fun loginUser(userCredentials: AuthRequest<Login>): AuthResponse<User> {
        return  client.post {
            url {
                path("users", "login")
            }
            contentType(ContentType.Application.Json)
            setBody(userCredentials)
        }.getBodyIfSuccess()
    }
}

suspend inline fun<reified T> HttpResponse.getBodyIfSuccess(): T {
    when (status.value) {
        in 200..299 -> {
            return body()
        }
        422 -> {
            if (this.bodyAsText().contains("invalid password")) {
                throw InvalidPasswordException
            }
            if (this.bodyAsText().contains("unknown email")) {
                throw UnknownEmailException
            }
            if (this.bodyAsText().contains("already taken")) {
                throw EmailAddressTakenException
            }
            throw Exception("${status.value} ${bodyAsText()}")
        }
        500 -> {
            throw ServerIssueException
        }
        else -> throw Exception("${status.value} ${bodyAsText()}")
    }
}

object InvalidPasswordException : Exception("Wrong password")
object UnknownEmailException : Exception("Wrong password")
object EmailAddressTakenException : Exception("Email address already taken")
object ServerIssueException : Exception("Server issue")