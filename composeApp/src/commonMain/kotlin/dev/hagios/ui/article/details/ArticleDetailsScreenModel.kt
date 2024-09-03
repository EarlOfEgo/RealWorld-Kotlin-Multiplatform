package dev.hagios.ui.article.details

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.hagios.data.article.ArticleRepository
import dev.hagios.data.article.models.Article
import dev.hagios.ui.article.details.ArticleDetailsUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ArticleDetailsScreenModel(
    private val articleRepository: ArticleRepository
) : ScreenModel {

    private val _articleSlug: MutableStateFlow<String?> = MutableStateFlow(null)

    fun getArticle(slug: String) {
        _articleSlug.value = slug
    }

    val article = _articleSlug.filterNotNull().flatMapLatest {
        articleRepository.getArticleBySlug(it)
    }
        .map<Article, ArticleDetailsUiState>(::Success)
        .catch { emit(ArticleDetailsUiState.Error(it)) }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(), ArticleDetailsUiState.Loading)

}


sealed interface ArticleDetailsUiState {
    data object Loading : ArticleDetailsUiState
    data class Error(val throwable: Throwable) : ArticleDetailsUiState
    data class Success(val data: Article) : ArticleDetailsUiState
}