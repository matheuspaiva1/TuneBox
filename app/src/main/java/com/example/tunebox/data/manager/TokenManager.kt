package com.example.tunebox.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "spotify_prefs")
class TokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[ACCESS_TOKEN] }

    suspend fun saveTokens(accessToken: String, refreshToken: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            if (refreshToken != null) {
                preferences[REFRESH_TOKEN] = refreshToken
            }
        }
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[ACCESS_TOKEN] }
            .toString()
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
