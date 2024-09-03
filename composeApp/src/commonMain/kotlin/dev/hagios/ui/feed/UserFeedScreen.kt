package dev.hagios.ui.feed

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

data object UserFeedScreen: Screen {

    @Composable
    override fun Content() {
        Text("OINK!")
    }

}
