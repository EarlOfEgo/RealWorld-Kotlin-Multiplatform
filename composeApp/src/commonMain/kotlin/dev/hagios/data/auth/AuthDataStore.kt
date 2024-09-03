package dev.hagios.data.auth


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath

private lateinit var dataStore: DataStore<Preferences>

private val lock = SynchronizedObject()

/**
 * Gets the singleton DataStore instance, creating it if necessary.
 */
fun getDataStore(producePath: () -> String): DataStore<Preferences> =
    synchronized(lock) {
        if (::dataStore.isInitialized) {
            dataStore
        } else {
            PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
                .also { dataStore = it }
        }
    }

internal const val dataStoreFileName = "auth.preferences_pb"

class AuthDataStore(
    private val dataStore: DataStore<Preferences>
) {
    private val tokenKey = stringPreferencesKey("token")

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.map {
                it[tokenKey]
            }.collect {
                _userLoggedIn.value = it != null
            }
        }
    }

    suspend fun storeToken(token: String) {
        dataStore.edit {
            it[tokenKey] = token
            _userLoggedIn.value = true
        }
    }

    suspend fun invalidateToken() {
        dataStore.edit {
            it.remove(tokenKey)
            _userLoggedIn.value = false
        }
    }

    fun getToken(): Flow<String?> {
        return dataStore.data.map {
            it[tokenKey]
        }
    }

    private val _userLoggedIn = MutableStateFlow(false)
    val userLoggedIn: Flow<Boolean> = _userLoggedIn
}