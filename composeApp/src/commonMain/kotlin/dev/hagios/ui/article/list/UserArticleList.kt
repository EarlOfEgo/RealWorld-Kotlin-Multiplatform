package dev.hagios.ui.article.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.hagios.data.article.ArticleQuery
import dev.hagios.ui.article.create.CreateArticleScreen
import dev.hagios.ui.article.details.ArticleDetailsScreen
import org.koin.core.parameter.parametersOf

data class UserArticleList(val username: String): Tab {
    @Composable
    override fun Content() {
        val screenModel: UserArticlesScreenModel = getScreenModel { parametersOf(
            ArticleQuery.Author(
                username
            )
        ) }
        val navigator = LocalNavigator.currentOrThrow
        val articles = screenModel.pagingArticles.collectAsLazyPagingItems()
        ArticlesSuccessContent(
            articles,
            onArticleClick = { navigator.getRoot().push(ArticleDetailsScreen(it.slug)) },
            onCreateArticle = { navigator.getRoot().push(CreateArticleScreen) }
        )
    }


    override val options: TabOptions
        @Composable
        get() {
            val title = "Stories"

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = null
                )
            }
        }
}

fun Navigator.getRoot(): Navigator {
    return this.parent?.getRoot() ?: this
}