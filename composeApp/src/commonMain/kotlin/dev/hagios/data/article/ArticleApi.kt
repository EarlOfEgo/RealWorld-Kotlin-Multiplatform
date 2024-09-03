package dev.hagios.data.article

import dev.hagios.data.article.models.Article
import dev.hagios.data.article.models.ArticleRequest
import dev.hagios.data.article.models.ArticleResponse
import dev.hagios.data.article.models.Articles
import dev.hagios.data.auth.getBodyIfSuccess
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

interface ArticleApi {
    suspend fun getArticles(page: Int, query: ArticleQuery? = null): Articles

    suspend fun getArticle(slug: String): Article
    suspend fun createArticle(articleRequest: ArticleRequest): Article
}

class UnauthorizedArticleApi(private val client: HttpClient) : ArticleApi {

    override suspend fun getArticles(page: Int, query: ArticleQuery?): Articles {
        return client.get {
            url {
                path("articles")
                parameter("page", page)
                query?.let { parameter(it.name, it.value) }
            }
        }.getBodyIfSuccess<Articles>()
    }

    override suspend fun getArticle(slug: String): Article {
        return client.get {
            url { path("articles", slug) }
        }.getBodyIfSuccess<ArticleResponse>().article
    }

    override suspend fun createArticle(
        articleRequest: ArticleRequest
    ): Article {
        throw IllegalStateException("Unauthorized")
    }
}

class AuthorizedArticleApi(private val client: HttpClient) : ArticleApi {

    override suspend fun getArticles(page: Int, query: ArticleQuery?): Articles {
        return client.get {
            url {
                path("articles")
                parameter("page", page)
                query?.let { parameter(it.name, it.value) }
            }
        }.getBodyIfSuccess<Articles>()
    }

    override suspend fun getArticle(slug: String): Article {
        return client.get {
            url { path("articles", slug) }
        }.getBodyIfSuccess<ArticleResponse>().article
    }

    override suspend fun createArticle(
        articleRequest: ArticleRequest
    ): Article {
        return client.post {
            url { path("articles") }
            setBody(articleRequest)
            contentType(ContentType.Application.Json)
        }.getBodyIfSuccess<ArticleResponse>().article
    }
}
