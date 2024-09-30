package dev.hagios.ui.article.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import dev.hagios.data.article.models.Article
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import realworld.composeapp.generated.resources.Res
import realworld.composeapp.generated.resources.all_articles_list_screen_success_content_description
import realworld.composeapp.generated.resources.edit_square_24dp
import realworld.composeapp.generated.resources.generic_error_label

@Composable
fun ArticlesSuccessContent(
    articles: LazyPagingItems<Article>,
    onArticleClick: (Article) -> Unit,
    onCreateArticle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            items(count = articles.itemCount,
                key = articles.itemKey { it.slug },
                contentType = articles.itemContentType { "Repos" }) { index ->
                val article = articles[index]
                article?.let {
                    ArticleListItem(it, modifier = Modifier.clickable { onArticleClick(article) })
                    Divider(modifier = Modifier.height(1.dp).fillMaxWidth())
                }
            }
            if (articles.loadState.errors()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(Res.string.generic_error_label))
                        Button({ articles.retry() }) {
                            Text("retry")
                        }
                    }

                }
            }
            if (articles.loadState.loading()) {
                item {
                    CircularProgressIndicator()
                }
            }
        }
        FloatingActionButton(
            onClick = {
                onCreateArticle()
            }, modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp),
            shape = MaterialTheme.shapes.medium.copy(
                CornerSize(percent = 50)
            ),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        ) {
            Icon(
                painterResource(Res.drawable.edit_square_24dp),
                contentDescription = stringResource(Res.string.all_articles_list_screen_success_content_description)
            )
        }
    }
}