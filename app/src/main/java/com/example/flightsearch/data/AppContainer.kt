package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val AIRPORT_SEARCH_VALUE = "airport_search_value"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = AIRPORT_SEARCH_VALUE
)

interface AppContainer {
    val flightSearchRepository: FlightSearchRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val flightSearchRepository: FlightSearchRepository by lazy {
        val db = FlightSearchDatabase.getDatabase(context)
        CurrentFlightSearchRepository(
            db.airportDao(),
            db.favoriteDao(),
            db.flightDao()
        )
    }
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}