package com.example.stocklocal.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "stocklocal_prefs")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val PIN_KEY = stringPreferencesKey("user_pin")
        val BUSINESS_NAME_KEY = stringPreferencesKey("business_name")
        val CURRENCY_KEY = stringPreferencesKey("currency")
        val TOKEN_KEY = stringPreferencesKey("api_token")
    }

    val userPin: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[PIN_KEY] ?: "" }

    val businessName: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[BUSINESS_NAME_KEY] ?: "Mi Negocio" }

    val currency: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[CURRENCY_KEY] ?: "MXN" }

    val apiToken: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[TOKEN_KEY] ?: "" }

    suspend fun savePin(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[PIN_KEY] = hashPin(pin)
        }
    }

    fun isPinValid(enteredPin: String, savedPin: String): Boolean {
        if (savedPin.isBlank()) return false

        val enteredHash = hashPin(enteredPin)

        return enteredHash == savedPin || enteredPin == savedPin
    }

    suspend fun saveBusinessName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[BUSINESS_NAME_KEY] = name
        }
    }

    suspend fun saveCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearPin() {
        context.dataStore.edit { preferences ->
            preferences.remove(PIN_KEY)
        }
    }

    suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun hashPin(pin: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(pin.toByteArray())

        return bytes.joinToString("") { byte ->
            "%02x".format(byte)
        }
    }
}