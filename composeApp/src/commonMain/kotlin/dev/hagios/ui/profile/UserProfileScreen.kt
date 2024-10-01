package dev.hagios.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.hagios.data.auth.models.User
import dev.hagios.ui.article.list.UserArticleList
import dev.hagios.ui.article.list.getRoot
import dev.hagios.ui.profile.edit.EditProfileScreen
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import realworld.composeapp.generated.resources.Res
import realworld.composeapp.generated.resources.edit_square_24dp
import realworld.composeapp.generated.resources.user_profile_screen_about_no_content_description1
import realworld.composeapp.generated.resources.user_profile_screen_about_no_content_description2
import realworld.composeapp.generated.resources.user_profile_screen_about_title
import realworld.composeapp.generated.resources.user_profile_screen_edit_profile_button
import realworld.composeapp.generated.resources.user_profile_screen_title
import realworld.composeapp.generated.resources.user_profile_screen_user_image_content_description

data object UserProfileScreen : Tab {

    @Composable
    override fun Content() {
        val screenModel: UserProfileScreenModel = getScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        when (val state = screenModel.profile.collectAsState().value) {
            is UserProfileUiState.Error -> Text(state.toString())
            UserProfileUiState.Loading -> CircularProgressIndicator()
            is UserProfileUiState.Success -> UserProfileSuccessContent(
                state.data,
                goToSettings = {},
                editProfile = {
                    navigator.getRoot().push(EditProfileScreen)
                })
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.user_profile_screen_title)
            val icon = rememberVectorPainter(Icons.Default.Person)

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
private fun UserProfileSuccessContent(
    user: User,
    goToSettings: () -> Unit,
    editProfile: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = goToSettings,
            modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
        ) {
//            Icon(Icons.Default.Settings, contentDescription = stringResource(Res.string.user_profile_screen_settings_button_content_description))
        }
        Column(modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()) {
            Row {
                val painter =
                    user.image?.let { asyncPainterResource(data = user.image) } ?: Resource.Success(
                        painterResource(
                            Res.drawable.edit_square_24dp
                        )
                    )
                KamelImage(
                    resource = painter,
                    contentDescription = stringResource(Res.string.user_profile_screen_user_image_content_description),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Row {
                    Text(user.username, style = MaterialTheme.typography.h5)
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = editProfile) {
                Text(stringResource(Res.string.user_profile_screen_edit_profile_button))
            }
            Spacer(Modifier.height(48.dp))

        }
        TabNavigator(UserArticleList(user.username)) { navigator ->
            Box(contentAlignment = Alignment.BottomStart) {
                Row(modifier = Modifier.padding(horizontal = 32.dp)) {
                    TabTextButton(navigator, UserArticleList(user.username))
                    Spacer(modifier = Modifier.width(32.dp))
                    TabTextButton(navigator, UserBio(user.bio))
                }
                Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
            }
            CurrentTab()
        }
    }
}

@Composable
private fun TabTextButton(navigator: TabNavigator, tab: Tab) {
    Column(modifier = Modifier.width(IntrinsicSize.Max).clickable { navigator.current = tab }) {
        Text(
            tab.options.title,
            style = MaterialTheme.typography.caption.copy(fontWeight = if (navigator.current.key == tab.key) FontWeight.Bold else FontWeight.Normal)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            modifier = Modifier.height(1.dp).fillMaxWidth(),
            if (navigator.current.key == tab.key) MaterialTheme.colors.onSurface.copy(alpha = 1f) else MaterialTheme.colors.onSurface.copy(
                alpha = 0f
            )
        )
    }
}

data class UserBio(val bio: String?) : Tab {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Box(modifier = Modifier.fillMaxSize().padding(32.dp)) {
            if (bio != null) {
                Text(
                    bio, style = MaterialTheme.typography.body1, modifier = Modifier.align(
                        Alignment.CenterStart
                    )
                )
            } else {
                Column(
                    modifier = Modifier.align(
                        Alignment.Center
                    ).clickable {
                        navigator.getRoot().push(EditProfileScreen)
                    }
                ) {
                    Text(
                        stringResource(Res.string.user_profile_screen_about_no_content_description1),
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        stringResource(Res.string.user_profile_screen_about_no_content_description2),
                        style = MaterialTheme.typography.caption,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.user_profile_screen_about_title)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = null
                )
            }
        }

}