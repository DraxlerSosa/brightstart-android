package com.brightstart.app.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    companion object {
        val KEY_USER_ID      = intPreferencesKey("user_id")
        val KEY_USER_NAME    = stringPreferencesKey("user_name")
        val KEY_USERNAME     = stringPreferencesKey("username")
        val KEY_USER_EMAIL   = stringPreferencesKey("user_email")
        val KEY_USER_ROLE    = stringPreferencesKey("user_role")
        val KEY_TOKEN        = stringPreferencesKey("auth_token")
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }
    val userId: Flow<Int>         = context.dataStore.data.map { it[KEY_USER_ID] ?: 0 }
    val userName: Flow<String>    = context.dataStore.data.map { it[KEY_USER_NAME] ?: "" }
    val username: Flow<String>    = context.dataStore.data.map { it[KEY_USERNAME] ?: "" }
    val userRole: Flow<String>    = context.dataStore.data.map { it[KEY_USER_ROLE] ?: "" }
    val userEmail: Flow<String>   = context.dataStore.data.map { it[KEY_USER_EMAIL] ?: "" }

    suspend fun token(): String? = context.dataStore.data.first()[KEY_TOKEN]

    suspend fun saveSession(id: Int, name: String, username: String, email: String, role: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID]      = id
            prefs[KEY_USER_NAME]    = name
            prefs[KEY_USERNAME]     = username
            prefs[KEY_USER_EMAIL]   = email
            prefs[KEY_USER_ROLE]    = role
            prefs[KEY_TOKEN]        = token
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun updateProfile(name: String, username: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USERNAME]  = username
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
