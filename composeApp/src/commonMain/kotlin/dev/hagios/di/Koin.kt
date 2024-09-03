package dev.hagios.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.hagios.data.article.ArticleApi
import dev.hagios.data.article.ArticlePagingSource
import dev.hagios.data.article.ArticleQuery
import dev.hagios.data.article.ArticleRepository
import dev.hagios.data.article.AuthorizedArticleApi
import dev.hagios.data.article.UnauthorizedArticleApi
import dev.hagios.data.auth.AuthApi
import dev.hagios.data.auth.AuthDataStore
import dev.hagios.data.auth.AuthRepository
import dev.hagios.data.auth.KtorAuthApi
import dev.hagios.data.user.KtorUserApi
import dev.hagios.data.user.UserApi
import dev.hagios.data.user.UserRepository
import dev.hagios.ui.MainScreenModel
import dev.hagios.ui.article.create.CreateArticleScreenModel
import dev.hagios.ui.article.details.ArticleDetailsScreenModel
import dev.hagios.ui.article.list.AllArticleScreenModel
import dev.hagios.ui.article.list.UserArticlesScreenModel
import dev.hagios.ui.auth.login.LoginScreenModel
import dev.hagios.ui.auth.signup.SignupScreenModel
import dev.hagios.ui.user.UserProfileScreenModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val default = "DefaultClient"

private const val authorized = "AuthorizedClient"

fun dataModule(dataStore: DataStore<Preferences>) = module {

    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
    single(named(default)) {
        HttpClient {
            install(ContentNegotiation) {
                json(get(), contentType = ContentType.Application.Json)
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.realworld.io"
                    path("api/")
                }
            }
        }
    }

    single(named(authorized)) {
        val token = get<AuthRepository>().accessToken
        get<HttpClient>(named(default)).config {
            defaultRequest {
                header("Authorization", "Token $token")
            }
        }
    }

    single<AuthApi> { KtorAuthApi(get(named(default))) }
    factory {
        AuthRepository(get(), get())
    }
    single {
        AuthDataStore(dataStore)
    }
    factory {
        ArticleRepository(
            unauthorizedArticleApi = get(named(default)),
            authorizedArticleApi = get(named(authorized))
        )
    }
    single<ArticleApi>(named(default)) { UnauthorizedArticleApi(get(named(default))) }
    single<ArticleApi>(named(authorized)) { AuthorizedArticleApi(get(named(authorized))) }

    factory(named(default)) { (query: ArticleQuery?) ->
        ArticlePagingSource(get(named(default)), query)
    }

    factory(named(authorized)) { (query: ArticleQuery?) ->
        ArticlePagingSource(get(named(authorized)), query)
    }

    factory { UserRepository(get()) }
    single<UserApi> { KtorUserApi(get(named(authorized))) }
}

val screenModelsModule = module {
    factoryOf(::SignupScreenModel)
    factoryOf(::LoginScreenModel)
    factoryOf(::ArticleDetailsScreenModel)
    factoryOf(::MainScreenModel)
    factoryOf(::UserProfileScreenModel)
    factoryOf(::CreateArticleScreenModel)
    factory { (query: ArticleQuery?) ->
        UserArticlesScreenModel(get(named(authorized)) { parametersOf(query) })
    }
    factory { AllArticleScreenModel(get(named(default)) { parametersOf(null) }) }
}

fun initKoin(dataStore: DataStore<Preferences>) {
    startKoin {
        modules(
            dataModule(dataStore),
            screenModelsModule,
        )
    }
}