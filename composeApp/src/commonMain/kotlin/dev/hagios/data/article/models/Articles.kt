package dev.hagios.data.article.models

import kotlinx.serialization.Serializable

@Serializable
data class Articles(
    val articles: List<Article>,
    val articlesCount: Int
)