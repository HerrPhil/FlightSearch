package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class CurrentFlightSearchRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao,
    private val flightDao: FlightDao
) : FlightSearchRepository {

    override fun getAirportsBy(searchValue: String): Flow<List<Airport>> = airportDao.getAirportsBy(searchValue)

    override fun countFavoritesFlow(): Flow<Int> = favoriteDao.countFavoritesFlow()

    override fun countFavorites(): Int = favoriteDao.countFavorites()

    override fun hasFavorites(): Boolean = favoriteDao.hasFavorites()

    override fun hasFavorite(departureCode: String, destinationCode: String): Boolean = favoriteDao.hasFavorite(departureCode, destinationCode)

    override suspend fun insert(favorite: Favorite) = favoriteDao.insert(favorite)

    override suspend fun deleteBy(departureCode: String, destinationCode: String) = favoriteDao.deleteBy(departureCode, destinationCode)

    override fun getPossibleFlights(departureCode: String): Flow<List<Flight>> = flightDao.getPossibleFlights(departureCode)

    override fun getFavoriteFlights(): Flow<List<Flight>> = flightDao.getFavoriteFlights()
}
