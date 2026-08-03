package com.st10445368.gitbbc.auth


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("biometric_prefs")

class BiometricPreferencesRepository(private val context: Context) {
    private val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")

    // Key rule: Initial state as null to distinguish loading state from disabled state
    val enabled: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED_KEY] ?: false
    }

    val enabledRaw: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED_KEY]
    }

    suspend fun setEnabled(isEnabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BIOMETRIC_ENABLED_KEY] = isEnabled
        }
    }
}