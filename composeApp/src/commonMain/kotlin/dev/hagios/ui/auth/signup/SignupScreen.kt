package dev.hagios.ui.auth.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.hagios.ui.auth.SecondaryTextFieldDefaults
import dev.hagios.ui.auth.login.LoginScreen
import dev.hagios.ui.profile.UserProfileScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import realworld.composeapp.generated.resources.Res
import realworld.composeapp.generated.resources.signup_screen_create_account_button
import realworld.composeapp.generated.resources.signup_screen_email_label
import realworld.composeapp.generated.resources.signup_screen_email_placeholder
import realworld.composeapp.generated.resources.signup_screen_go_to_login_title1
import realworld.composeapp.generated.resources.signup_screen_go_to_login_title2
import realworld.composeapp.generated.resources.signup_screen_headline
import realworld.composeapp.generated.resources.signup_screen_name_label
import realworld.composeapp.generated.resources.signup_screen_name_placeholder
import realworld.composeapp.generated.resources.signup_screen_password_label
import realworld.composeapp.generated.resources.signup_screen_password_placeholder
import realworld.composeapp.generated.resources.signup_screen_title
import realworld.composeapp.generated.resources.visibility_24dp
import realworld.composeapp.generated.resources.visibility_off_24dp

data object SignupScreen : Tab {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: SignupScreenModel = getScreenModel()

        Box(modifier = Modifier.fillMaxSize()) {
            if (screenModel.loading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Center))
                }
            }
            SignupScreenContent(screenModel, onSignInClicked = { navigator.replace(LoginScreen) })
        }
        val value = screenModel.registerRequest.collectAsState().value
        LaunchedEffect(value) {
            value?.let {
                navigator.replace(UserProfileScreen)
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.signup_screen_title)
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
private fun SignupScreenContent(
    screenModel: SignupScreenModel,
    onSignInClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onEmailChanged: (String) -> Unit = screenModel::updateEmail,
    onNameChanged: (String) -> Unit = screenModel::updateName,
    onPasswordChanged: (String) -> Unit = screenModel::updatePassword,
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.align(CenterHorizontally),
            text = stringResource(Res.string.signup_screen_headline),
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(stringResource(Res.string.signup_screen_name_label))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenModel.username,
            onValueChange = onNameChanged,
            singleLine = true,
            placeholder = {
                Text(stringResource(Res.string.signup_screen_name_placeholder))
            },
            colors = SecondaryTextFieldDefaults(),
        )
        Text(stringResource(Res.string.signup_screen_email_label))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenModel.email,
            onValueChange = onEmailChanged,
            singleLine = true,
            placeholder = {
                Text(stringResource(Res.string.signup_screen_email_placeholder))
            },
            colors = SecondaryTextFieldDefaults(),
            isError = screenModel.emailError
        )
        var showPassword by rememberSaveable { mutableStateOf(false) }
        Text(stringResource(Res.string.signup_screen_password_label))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenModel.password,
            onValueChange = onPasswordChanged,
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon =
                    if (showPassword) painterResource(Res.drawable.visibility_off_24dp) else painterResource(
                        Res.drawable.visibility_24dp
                    )
                Icon(icon, null, modifier = Modifier.clickable { showPassword = !showPassword })
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            placeholder = {
                Text(stringResource(Res.string.signup_screen_password_placeholder))
            },
            colors = SecondaryTextFieldDefaults(),
        )
        Spacer(Modifier.height(32.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = screenModel::registerUser,
            enabled = screenModel.allowSignup.collectAsState().value
        ) {
            Text(stringResource(Res.string.signup_screen_create_account_button))
        }
        Spacer(Modifier.height(32.dp))
        Row(modifier = Modifier.clickable {
            onSignInClicked()
        }.align(alignment = CenterHorizontally)) {
            Text(stringResource(Res.string.signup_screen_go_to_login_title1))
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(Res.string.signup_screen_go_to_login_title2), color = MaterialTheme.colors.primary)
        }
    }
}