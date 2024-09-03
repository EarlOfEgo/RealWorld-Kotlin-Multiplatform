package dev.hagios.ui.article.list

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.hagios.data.article.ArticlePagingSource

class UserArticlesScreenModel(
    pagingSource: ArticlePagingSource
): ScreenModel {
    val pagingArticles = Pager(
        PagingConfig(pageSize = 10)
    ) {
        pagingSource
    }.flow
        .cachedIn(screenModelScope)
}