package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {

    // functions for the airport DAO
    fun getAirportsBy(searchValue: String): Flow<List<Airport>>

    // functions for the favorite DAO
    fun countFavoritesFlow(): Flow<Int>

    fun countFavorites(): Int

    fun hasFavorites(): Boolean

    fun hasFavorite(departureCode: String, destinationCode: String): Boolean

    suspend fun insert(favorite: Favorite)

    suspend fun deleteBy(departureCode: String, destinationCode: String)

    // functions for flight DAO
    fun getPossibleFlights(departureCode: String): Flow<List<Flight>>

    fun getFavoriteFlights(): Flow<List<Flight>>
}