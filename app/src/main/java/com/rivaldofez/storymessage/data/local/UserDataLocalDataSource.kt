package com.rivaldofez.storymessage.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataLocalDataSource @Inject constructor(private val dataStore: DataStore<Preferences>) {

    fun getAuthenticationToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    fun getThemeSetting(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY]
        }
    }

    suspend fun saveAuthenticationToken(token: String){
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun removeAuthenticationToken(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveThemeSetting(themeId: Int){
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeId.toString()
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token_key")
        private val THEME_KEY = stringPreferencesKey("theme_key")
    }
}