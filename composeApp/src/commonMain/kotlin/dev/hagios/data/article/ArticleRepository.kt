package dev.hagios.data.article

import dev.hagios.data.article.models.Article
import dev.hagios.data.article.models.ArticleRequest
import kotlinx.coroutines.flow.flow

class ArticleRepository(
    private val unauthorizedArticleApi: ArticleApi,
    private val authorizedArticleApi: ArticleApi
) {

    fun getArticleBySlug(slug: String) = flow {
        emit(unauthorizedArticleApi.getArticle(slug))
    }

    suspend fun createArticle(
        title: String,
        description: String,
        body: String,
        filter: List<String>
    ): Article {
        val article = ArticleRequest(
            ArticleRequest.Article(
                title = title,
                description = description,
                body = body,
                tagList = filter
            )
        )
        return authorizedArticleApi.createArticle(article)
    }

}