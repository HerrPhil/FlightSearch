package com.example.flightsearch.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// DataStore stores key-value pairs.
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val CURRENT_AIRPORT_SEARCH_VALUE = stringPreferencesKey("current_airport_search_value")
        const val TAG = "UserPreferencesRepo"
    }

    val currentAirportSearchValue: Flow<String> = dataStore
        .data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
        preferences[CURRENT_AIRPORT_SEARCH_VALUE] ?: ""
    }

    suspend fun saveAirportPreference(searchValue: String) {
        dataStore.edit {preferences ->
            preferences[CURRENT_AIRPORT_SEARCH_VALUE] = searchValue
        }
    }
}