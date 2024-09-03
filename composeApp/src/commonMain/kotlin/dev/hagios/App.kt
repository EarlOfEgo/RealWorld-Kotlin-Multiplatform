package dev.hagios

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cafe.adriel.voyager.navigator.Navigator
import dev.hagios.di.initKoin
import dev.hagios.ui.MainScreen
import dev.hagios.ui.theme.ConduitTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(dataStore: DataStore<Preferences>) {
    initKoin(dataStore)
    ConduitTheme {
        Navigator(MainScreen)
    }
}