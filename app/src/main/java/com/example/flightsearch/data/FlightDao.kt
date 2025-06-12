package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {

    @Query(
        "SELECT departure_airport.iata_code AS departureIataCode, " +
                "departure_airport.name AS departureAirportName, " +
                "arrival_airport.iata_code AS arrivalIataCode, " +
                "arrival_airport.name AS arrivalAirportName, " +
                "EXISTS (SELECT 1 FROM favorite " +
                "WHERE departure_code = departure_airport.iata_code " +
                "AND destination_code = arrival_airport.iata_code) AS isFavorite " +
                "FROM airport AS departure_airport, " +
                "airport AS arrival_airport " +
                "WHERE departure_airport.iata_code = :departureCode " +
                "AND departure_airport.iata_code <> arrival_airport.iata_code"
    )
    fun getPossibleFlights(departureCode: String): Flow<List<Flight>>

    @Query(
        "SELECT departure_airport.iata_code AS departureIataCode, " +
                "departure_airport.name AS departureAirportName, " +
                "arrival_airport.iata_code AS arrivalIataCode, " +
                "arrival_airport.name AS arrivalAirportName, " +
                "1 AS isFavorite " +
                "FROM favorite " +
                "INNER JOIN airport AS departure_airport ON favorite.departure_code = departure_airport.iata_code " +
                "INNER JOIN airport AS arrival_airport ON favorite.destination_code = arrival_airport.iata_code"
    )
    fun getFavoriteFlights(): Flow<List<Flight>>

}