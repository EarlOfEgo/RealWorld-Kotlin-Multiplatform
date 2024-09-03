package dev.hagios.data.article.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Article(
    val author: Author,
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val description: String,
    val favorited: Boolean,
    val favoritesCount: Int,
    val slug: String,
    val tagList: List<String>,
    val title: String,
    val body: String?,
    @Serializable(LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime?
)

@Serializable
data class ArticleResponse(val article: Article)

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString()
        return LocalDateTime.parse(string.dropLast(1))
    }
}

val LocalDateTime.formatted: String
    get() {
        return format(LocalDateTime.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            chars(" ")
            dayOfMonth()
            chars(", ")
            year()
        })
    }