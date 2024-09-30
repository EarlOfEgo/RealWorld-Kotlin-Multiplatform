package dev.hagios.ui.article.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.hagios.data.article.models.Article
import dev.hagios.data.article.models.formatted
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import realworld.composeapp.generated.resources.Res
import realworld.composeapp.generated.resources.article_details_screen_image_content_description
import realworld.composeapp.generated.resources.edit_square_24dp
import realworld.composeapp.generated.resources.generic_back_button_label

data class ArticleDetailsScreen(val slug: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: ArticleDetailsScreenModel = getScreenModel()
        screenModel.getArticle(slug)
        Scaffold(topBar = {
            TopAppBar(backgroundColor = Color.White, elevation = 0.dp) {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.generic_back_button_label))
                }
            }
        }) { paddingValues ->
            ArticleDetailsScreenContent(screenModel, modifier = Modifier.padding(paddingValues))
        }
    }

}


@Composable
private fun ArticleDetailsScreenContent(
    screenModel: ArticleDetailsScreenModel,
    modifier: Modifier = Modifier
) {
    when (val state = screenModel.article.collectAsState().value) {
        is ArticleDetailsUiState.Error -> Text(state.toString())
        ArticleDetailsUiState.Loading -> CircularProgressIndicator()
        is ArticleDetailsUiState.Success -> ArticleDetailsSuccessContent(state.data, modifier)
    }

}

@Composable
fun ArticleDetailsSuccessContent(article: Article, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 48.dp).verticalScroll(rememberScrollState())) {
        Text(article.title, style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            val painter = article.author.image?.let { asyncPainterResource(data = it) } ?: Resource.Success(
                painterResource(
                Res.drawable.edit_square_24dp)
            )
            KamelImage(
                resource = painter,
                contentDescription = stringResource(Res.string.article_details_screen_image_content_description),
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(article.author.username, style = MaterialTheme.typography.h6)
                Text(article.createdAt.formatted)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        article.body?.let { Text(it.replace("\\n", "\n"), style = MaterialTheme.typography.body1) }
    }
}
