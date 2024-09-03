package dev.hagios.ui.article.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.hagios.data.article.ArticleRepository
import dev.hagios.data.article.models.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreateArticleScreenModel(
    private val articleRepository: ArticleRepository,
) : ScreenModel {
    var tags by mutableStateOf("")
        private set

    var body by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var title by mutableStateOf("")
        private set

    val allowPublish: StateFlow<Boolean> =
        combine(
            snapshotFlow { title },
            snapshotFlow { description },
            snapshotFlow { body }) { title, description, body ->
            title.isNotBlank() && description.isNotBlank() && body.isNotBlank()
        }
            .stateIn(
                scope = screenModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    fun updateBody(input: String) {
        body = input
    }

    fun updateTitle(input: String) {
        title = input
    }

    fun updateDescription(input: String) {
        description = input
    }

    fun updateTags(input: String) {
        tags = input
    }

    private val _createArticleRequest = MutableStateFlow<Article?>(null)

    val createArticleRequest = _createArticleRequest.asStateFlow()

    fun publish() {
        screenModelScope.launch {
            try {
                val article = articleRepository.createArticle(
                    title,
                    description,
                    body,
                    tags.split(",").map { it.trim() }.filter { it.isNotBlank() })
                _createArticleRequest.emit(article)
            } catch (e: Exception) {
                println(e)
            }
        }
    }


}