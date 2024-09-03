package dev.hagios.ui.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
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
import dev.hagios.ui.auth.signup.SignupScreen
import dev.hagios.ui.user.UserProfileScreen
import org.jetbrains.compose.resources.painterResource
import realworld.composeapp.generated.resources.Res
import realworld.composeapp.generated.resources.visibility_24dp
import realworld.composeapp.generated.resources.visibility_off_24dp

data object LoginScreen : Tab {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: LoginScreenModel = getScreenModel()
        LoginScreenContent(
            screenModel,
            onSignupClicked = {
                navigator.replace(SignupScreen)
            }
        )
        val value = screenModel.loginRequest.collectAsState(null).value
        LaunchedEffect(value) {
            value?.let {
                navigator.replace(UserProfileScreen)
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "Login"
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
private fun LoginScreenContent(
    screenModel: LoginScreenModel,
    onSignupClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onEmailChanged: (String) -> Unit = screenModel::updateEmail,
    onPasswordChanged: (String) -> Unit = screenModel::updatePassword,
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.align(CenterHorizontally),
            text = "Sign in with email",
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text("Your email")
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenModel.email,
            onValueChange = onEmailChanged,
            singleLine = true,
            placeholder = {
                Text("janedoe@email.com")
            },
            colors = SecondaryTextFieldDefaults(),
        )
        var showPassword by rememberSaveable { mutableStateOf(false) }
        Text("Your password")
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
                Text("password")
            },
            colors = SecondaryTextFieldDefaults(),
        )
        Spacer(Modifier.height(32.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = screenModel::loginUser,
            enabled = screenModel.allowLogin.collectAsState().value
        ) {
            Text("Login")
        }
        Spacer(Modifier.height(32.dp))
        Row(modifier = Modifier.clickable {
            onSignupClicked()
        }.align(alignment = Alignment.CenterHorizontally)) {
            Text("Don't have an account?")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Sign up", color = MaterialTheme.colors.primary)
        }
    }
}
