package dev.hagios.data.article.models

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val following: Boolean,
    val image: String?,
    val username: String
)