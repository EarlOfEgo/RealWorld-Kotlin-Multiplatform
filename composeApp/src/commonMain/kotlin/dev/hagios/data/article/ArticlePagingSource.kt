package dev.hagios.data.article

import androidx.paging.PagingState
import app.cash.paging.PagingSource
import dev.hagios.data.article.models.Article

class ArticlePagingSource(
    private val articleApi: ArticleApi,
    private val query: ArticleQuery?
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        try {
            val currentPageNumber = params.key ?: 1
            val articles = articleApi.getArticles(currentPageNumber, query).articles
            val nextPageNumber =
                if (currentPageNumber * params.loadSize == articles.size || articles.size < 10) {
                    null
                } else currentPageNumber + 1
            return LoadResult.Page(
                data = articles,
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}

sealed class ArticleQuery(val value: String, val name: String) {
    data class Favorited(val username: String): ArticleQuery(username, "favorited")
    data class Author(val authorName: String) : ArticleQuery(authorName, "author")
    data class Tag(val tagName: String) : ArticleQuery(tagName, "tag")
}