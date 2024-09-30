package dev.hagios.ui.article.create

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.hagios.ui.article.details.ArticleDetailsScreen
import org.jetbrains.compose.resources.stringResource
import realworld.composeapp.generated.resources.Res
import realworld.composeapp.generated.resources.create_article_screen_body_label
import realworld.composeapp.generated.resources.create_article_screen_body_placeholder
import realworld.composeapp.generated.resources.create_article_screen_description_label
import realworld.composeapp.generated.resources.create_article_screen_description_placeholder
import realworld.composeapp.generated.resources.create_article_screen_publish_button
import realworld.composeapp.generated.resources.create_article_screen_tags_label
import realworld.composeapp.generated.resources.create_article_screen_tags_placeholder
import realworld.composeapp.generated.resources.create_article_screen_title_label
import realworld.composeapp.generated.resources.create_article_screen_title_placeholder

data object CreateArticleScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: CreateArticleScreenModel = getScreenModel()
        Scaffold(topBar = {
            TopAppBar(backgroundColor = Color.White, elevation = 0.dp) {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
                }
            }
        }) { paddingValues ->
            CreateArticleScreenContent(screenModel, modifier = Modifier.padding(paddingValues))
        }

        val value = screenModel.createArticleRequest.collectAsState(null).value
        LaunchedEffect(value) {
            value?.let {
                navigator.replace(ArticleDetailsScreen(it.slug))
            }
        }
    }

}

@Composable
private fun CreateArticleScreenContent(screenModel: CreateArticleScreenModel, modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = modifier.padding(horizontal = 48.dp)) {
            OutlinedTextField(
                value = screenModel.title,
                onValueChange = screenModel::updateTitle,
                label = { Text(stringResource(Res.string.create_article_screen_title_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.create_article_screen_title_placeholder)) }
            )
            OutlinedTextField(
                value = screenModel.description,
                onValueChange = screenModel::updateDescription,
                label = { Text(stringResource(Res.string.create_article_screen_description_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.create_article_screen_description_placeholder)) }
            )
            OutlinedTextField(
                value = screenModel.body,
                onValueChange = screenModel::updateBody,
                label = { Text(stringResource(Res.string.create_article_screen_body_label)) },
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .scrollable(rememberScrollState(), orientation = Orientation.Vertical),
                placeholder = { Text(stringResource(Res.string.create_article_screen_body_placeholder)) }
            )
            OutlinedTextField(
                value = screenModel.tags,
                onValueChange = screenModel::updateTags,
                label = { Text(stringResource(Res.string.create_article_screen_tags_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.create_article_screen_tags_placeholder)) }
            )
            Button(
                onClick = { screenModel.publish() },
                modifier = Modifier.fillMaxWidth(),
                enabled = screenModel.allowPublish.collectAsState().value
            ) {
                Text(stringResource(Res.string.create_article_screen_publish_button))
            }
        }
    }
}
