package dev.hagios.ui.profile.edit

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
import org.jetbrains.compose.resources.stringResource
import realworld.composeapp.generated.resources.Res
import realworld.composeapp.generated.resources.edit_profile_screen_bio_label
import realworld.composeapp.generated.resources.edit_profile_screen_bio_placeholder
import realworld.composeapp.generated.resources.edit_profile_screen_email_label
import realworld.composeapp.generated.resources.edit_profile_screen_email_placeholder
import realworld.composeapp.generated.resources.edit_profile_screen_password_label
import realworld.composeapp.generated.resources.edit_profile_screen_password_placeholder
import realworld.composeapp.generated.resources.edit_profile_screen_picture_url_label
import realworld.composeapp.generated.resources.edit_profile_screen_picture_url_placeholder
import realworld.composeapp.generated.resources.edit_profile_screen_update_button
import realworld.composeapp.generated.resources.edit_profile_screen_username_label
import realworld.composeapp.generated.resources.edit_profile_screen_username_placeholder
import realworld.composeapp.generated.resources.generic_back_button_label

data object EditProfileScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: EditProfileScreenModel = getScreenModel()

        Scaffold(topBar = {
            TopAppBar(backgroundColor = Color.White, elevation = 0.dp) {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.generic_back_button_label))
                }
            }
        }) { paddingValues ->
            EditProfileScreenContent(screenModel, modifier = Modifier.padding(paddingValues))
        }

        val value = screenModel.updateUserRequest.collectAsState(null).value
        LaunchedEffect(value) {
            value?.let {
                navigator.pop()
            }
        }
    }

}

@Composable
private fun EditProfileScreenContent(screenModel: EditProfileScreenModel, modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = modifier.padding(horizontal = 48.dp)) {
            OutlinedTextField(
                value = screenModel.username,
                onValueChange = screenModel::updateUsername,
                label = { Text(stringResource(Res.string.edit_profile_screen_username_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.edit_profile_screen_username_placeholder)) }
            )
            OutlinedTextField(
                value = screenModel.pictureUrl,
                onValueChange = screenModel::updatePictureUrl,
                label = { Text(stringResource(Res.string.edit_profile_screen_picture_url_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.edit_profile_screen_picture_url_placeholder)) }
            )
            OutlinedTextField(
                value = screenModel.bio,
                onValueChange = screenModel::updateBio,
                label = { Text(stringResource(Res.string.edit_profile_screen_bio_label)) },
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .scrollable(rememberScrollState(), orientation = Orientation.Vertical),
                placeholder = { Text(stringResource(Res.string.edit_profile_screen_bio_placeholder)) }
            )
            OutlinedTextField(
                value = screenModel.email,
                onValueChange = screenModel::updateEmail,
                label = { Text(stringResource(Res.string.edit_profile_screen_email_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.edit_profile_screen_email_placeholder)) }
            )
            OutlinedTextField(
                value = screenModel.password,
                onValueChange = screenModel::updatePassword,
                label = { Text(stringResource(Res.string.edit_profile_screen_password_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.edit_profile_screen_password_placeholder)) }
            )
            Button(
                onClick = { screenModel.update() },
                modifier = Modifier.fillMaxWidth(),
                enabled = screenModel.allowUpdate.collectAsState().value
            ) {
                Text(stringResource(Res.string.edit_profile_screen_update_button))
            }
        }
    }

}