package dev.hagios.data.article.models

import kotlinx.serialization.Serializable

@Serializable
data class ArticleRequest(val article: Article) {
    @Serializable
    data class Article(
        val title: String,
        val description: String,
        val body: String,
        val tagList: List<String>
    )
}