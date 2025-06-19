package com.example.flightsearch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightsearch.data.AppContainer
import com.example.flightsearch.data.AppDataContainer
import com.example.flightsearch.data.UserPreferencesRepository

//private const val AIRPORT_SEARCH_VALUE = "airport_search_value"
//private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
//    name = AIRPORT_SEARCH_VALUE
//)

class FlightSearchApplication : Application() {
    lateinit var container: AppContainer
//    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
//        userPreferencesRepository = UserPreferencesRepository(dataStore)
        container = AppDataContainer(this)

    }
}