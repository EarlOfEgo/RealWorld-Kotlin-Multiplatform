package dev.hagios.ui.article.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.cash.paging.CombinedLoadStates
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.hagios.data.article.models.Article
import dev.hagios.data.article.models.Author
import dev.hagios.data.article.models.formatted
import dev.hagios.ui.article.create.CreateArticleScreen
import dev.hagios.ui.article.details.ArticleDetailsScreen
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import realworld.composeapp.generated.resources.Res
import realworld.composeapp.generated.resources.edit_square_24dp


data object ArticlesList : Tab {
    @Composable
    override fun Content() {
        val screenModel: AllArticleScreenModel = getScreenModel()
        val navigator = LocalNavigator.currentOrThrow
        AllArticlesScreenContent(screenModel, onArticleClick = {
            navigator.parent?.push(ArticleDetailsScreen(it.slug))
        }, onCreateArticle = {
            navigator.parent?.push(CreateArticleScreen)
        })
    }


    override val options: TabOptions
        @Composable
        get() {
            val title = "Articles"
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }
}


@Composable
private fun AllArticlesScreenContent(
    screenModel: AllArticleScreenModel,
    onArticleClick: (Article) -> Unit,
    onCreateArticle: () -> Unit
) {
    val articles = screenModel.pagingArticles.collectAsLazyPagingItems()
    ArticlesSuccessContent(
        articles,
        onArticleClick = onArticleClick,
        onCreateArticle = onCreateArticle
    )
}

fun CombinedLoadStates.errors(): Boolean {
    return this.append is LoadStateError || this.refresh is LoadStateError || this.prepend is LoadStateError
}

fun CombinedLoadStates.loading(): Boolean {
    return this.append is LoadStateLoading || this.refresh is LoadStateLoading || this.prepend is LoadStateLoading
}

@Composable
fun ArticleListItem(article: Article, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(48.dp)) {
        Author(article.author)
        Spacer(modifier = Modifier.height(32.dp))
        Text(article.title, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            article.description,
            style = MaterialTheme.typography.subtitle1,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(article.createdAt.formatted)
    }
}

@Composable
private fun Author(author: Author) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val painter =
            author.image?.let { asyncPainterResource(data = author.image) } ?: Resource.Success(
                painterResource(
                    Res.drawable.edit_square_24dp
                )
            )
        KamelImage(
            resource = painter,
            contentDescription = "Author image",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(author.username)
    }
}
