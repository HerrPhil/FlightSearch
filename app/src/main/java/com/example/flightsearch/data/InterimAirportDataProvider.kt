package com.example.flightsearch.data

import android.util.Log
import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.domain.FlightDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

object InterimAirportDataProvider {

    // Treat this as a table - never explicitly return this
    private val airports = getInterimAirportDetails()

    // Treat this as a table - never explicitly return this
    private val flights = getInterimFlightDetails()

    // Treat this as a table - never explicitly return this
    private val favorites = mutableListOf<FlightDetails>()

    // Flow (query) results of airport details
    private var airportDetailsBySearchValue: Flow<List<AirportDetails>> = emptyFlow()

    // Flow (query) results of flight details
    private var flightDetails: Flow<List<FlightDetails>> = emptyFlow()

    // TODO remove - only used in prototype app composable
    fun getAirports(): Flow<List<AirportDetails>> = flow {
        Log.i("uistate", "start InterimAirportDataProvider.getAirports()")
        // doing this to create a "results" response from the "table" list.
        // filter creates a new ArrayList().
        val queryResults = airports
            .filter {
                true
            }
        Log.i("uistate", "**** number of airports in query results to emit is ${queryResults.size}")
        emit(queryResults)
    }

    // TODO remove - only used in prototype app composable
    fun getFlights(): Flow<List<FlightDetails>> = flow {
        Log.i("uistate", "start InterimAirportDataProvider.getFlights()")
        // doing this to create a "results" response from the "table" list.
        // filter creates a new ArrayList().
        val queryResults = flights
            .filter {
                true
            }
        Log.i("uistate", "**** number of flights in query results to emit is ${queryResults.size}")
        emit(queryResults)
    }

    fun getAirportsBy(searchValue: String): Flow<List<AirportDetails>> {
        Log.i("uistate", "start InterimAirportDataProvider getAirportsBy($searchValue)")
        Log.i("uistate", "**** essentially build a new query response")
        clearAirportDetails()
        setAirportsBy(searchValue)
        return airportDetailsBySearchValue
    }

    fun getPossibleFlightsBy(departureIataCode: String): Flow<List<FlightDetails>> {
        Log.i(
            "uistate",
            "start InterimAirportDataProvider getPossibleFlightsBy($departureIataCode)"
        )
        Log.i("uistate", "**** essentially build a new query response")
        clearFlightDetails()
        setPossibleFlightsBy(departureIataCode)
        return flightDetails
    }

    fun getFavoriteFlights(): Flow<List<FlightDetails>> {
        Log.i("uistate", "start InterimAirportDataProvider getFavoriteFlights()")
        Log.i("uistate", "**** essentially build a new query response")
        clearFlightDetails()
        setFavoriteFlights()
        return flightDetails
    }

    fun getFavoriteFlightsCount(): Int {
        return favorites.size
    }

    fun exists(flightDetails: FlightDetails): Boolean {
        Log.i(
            "uistate",
            "start InterimAirportDataProvider.exists(${flightDetails.departureIataCode}, ${flightDetails.arrivalIataCode})"
        )
        return favorites.any { it.flightID == flightDetails.flightID }
    }

    fun hasFavorites(): Boolean {
        return favorites.isNotEmpty()
    }

    suspend fun addFavorite(flightDetails: FlightDetails) {
        Log.i(
            "uistate",
            "start InterimAirportDataProvider.addFavorite(${flightDetails.departureIataCode}, ${flightDetails.arrivalIataCode})"
        )
        delay(500)
        Log.i("uistate", "**** count before adding favorite flight = ${favorites.size}")
//        flightDetails.favorite = true
        favorites.add(flightDetails)
        Log.i("uistate", "**** count after adding favorite flight = ${favorites.size}")
    }

    suspend fun removeFavorite(flightDetails: FlightDetails) {
        Log.i(
            "uistate",
            "start InterimAirportDataProvider.removeFavorite(${flightDetails.departureIataCode}, ${flightDetails.arrivalIataCode})"
        )
        delay(500)
        Log.i("uistate", "**** count before removing favorite flight = ${favorites.size}")
//        flightDetails.favorite = false
        favorites.removeIf { it.flightID == flightDetails.flightID }
        Log.i("uistate", "**** count after removing favorite flight = ${favorites.size}")
    }

    private fun clearAirportDetails() {
        airportDetailsBySearchValue = emptyFlow()
    }

    private fun setAirportsBy(searchValue: String) {
        Log.i("uistate", "start InterimAirportDataProvider.setAirportsBy($searchValue)")
        airportDetailsBySearchValue = flow {
            Log.i(
                "uistate",
                "start InterimAirportDataProvider.setAirportsBy($searchValue) start the flow (query)"
            )
            // TODO Room DB query will use WHERE/LIKE clause with search value parameter
            //      doing this to create a "results" response from the "table" list.
            //      filter creates a new ArrayList().
            val queryResults: List<AirportDetails> = airports
                .filter { airportDetails: AirportDetails ->
                    Log.i("uistate", "filter InterimAirportDataProvider.getAirportsBy filter")
                    searchValue.isNotBlank() &&
                            (airportDetails.name.contains(
                                other = searchValue,
                                ignoreCase = true
                            ) || airportDetails.iataCode.contains(
                                other = searchValue,
                                ignoreCase = true
                            ))
                }
            Log.i(
                "uistate",
                "**** number of airports in query results to emit is ${queryResults.size}"
            )
            emit(queryResults)
        }
    }

    private fun clearFlightDetails() {
        flightDetails = emptyFlow()
    }

    private fun setPossibleFlightsBy(departureIataCode: String) {
        flightDetails = flow {
            Log.i(
                "uistate",
                "start InterimAirportDataProvider.getPossibleFlightsBy($departureIataCode)"
            )
            // TODO Room DB query will use WHERE/LIKE clause with departure Iata code parameter
            //      doing this to create a "results" response from the "table" list.
            //      filter creates a new ArrayList().
            val queryResults = flights
                .filter { flightDetails: FlightDetails ->
                    flightDetails.departureIataCode.contains(
                        other = departureIataCode,
                        ignoreCase = true
                    )
                }
            Log.i(
                "uistate",
                "**** number of possible flights in query results to emit is ${queryResults.size}"
            )
            emit(queryResults)
        }
    }

    private fun setFavoriteFlights() {
        Log.i("uistate", "start InterimAirportDataProvider.setFavoriteFlights()")
        flightDetails = flow {
            Log.i(
                "uistate",
                "start InterimAirportDataProvider.setFavoriteFlights() start the flow query"
            )
            // TODO Room DB query will use WHERE clause to JOIN the favorites table.
            //       doing this to create a "results" response from the "table" list.
            //       filter creates a new ArrayList().
            val queryResults = favorites
                .filter {
                    true
                }
            Log.i(
                "uistate",
                "**** number of favorite flights in query results to emit is ${queryResults.size}"
            )
            emit(queryResults)
        }
    }

    private fun getInterimAirportDetails(): List<AirportDetails> {
        Log.i("uistate", "start InterimAirportDataProvider.getInterimAirportDetails()")
        return listOf(
            AirportDetails(
                id = 1,
                iataCode = "YYC",
                name = "Calgary International Airport",
                passengers = 100
            ),
            AirportDetails(
                id = 2,
                iataCode = "YEG",
                name = "Edmonton International Airport",
                passengers = 200
            ),
            AirportDetails(
                id = 3,
                iataCode = "YVR",
                name = "Vancouver International Airport",
                passengers = 300
            ),
            AirportDetails(
                id = 4,
                iataCode = "YYZ",
                name = "Toronto International Airport",
                passengers = 400
            ),
            AirportDetails(
                id = 5,
                iataCode = "YWG",
                name = "Winnipeg International Airport",
                passengers = 500
            ),
        )
    }

    private fun getInterimFlightDetails(): List<FlightDetails> {
        Log.i("uistate", "start InterimAirportDataProvider.getInterimFlightDetails()")
        return listOf(

            // Calgary departures
            FlightDetails(
                departureIataCode = "YYC",
                departureAirportName = "Calgary International Airport",
                arrivalIataCode = "YEG",
                arrivalAirportName = "Edmonton International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YYC",
                departureAirportName = "Calgary International Airport",
                arrivalIataCode = "YVR",
                arrivalAirportName = "Vancouver International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YYC",
                departureAirportName = "Calgary International Airport",
                arrivalIataCode = "YYZ",
                arrivalAirportName = "Toronto International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YYC",
                departureAirportName = "Calgary International Airport",
                arrivalIataCode = "YWG",
                arrivalAirportName = "Winnipeg International Airport",
                isFavorite = false
            ),

            // Edmonton departures
            FlightDetails(
                departureIataCode = "YEG",
                departureAirportName = "Edmonton International Airport",
                arrivalIataCode = "YYC",
                arrivalAirportName = "Calgary International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YEG",
                departureAirportName = "Edmonton International Airport",
                arrivalIataCode = "YVR",
                arrivalAirportName = "Vancouver International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YEG",
                departureAirportName = "Edmonton International Airport",
                arrivalIataCode = "YYZ",
                arrivalAirportName = "Toronto International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YEG",
                departureAirportName = "Edmonton International Airport",
                arrivalIataCode = "YWG",
                arrivalAirportName = "Winnipeg International Airport",
                isFavorite = false
            ),

            // Vancouver departures
            FlightDetails(
                departureIataCode = "YVR",
                departureAirportName = "Vancouver International Airport",
                arrivalIataCode = "YYC",
                arrivalAirportName = "Calgary International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YVR",
                departureAirportName = "Vancouver International Airport",
                arrivalIataCode = "YEG",
                arrivalAirportName = "Edmonton International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YVR",
                departureAirportName = "Vancouver International Airport",
                arrivalIataCode = "YYZ",
                arrivalAirportName = "Toronto International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YVR",
                departureAirportName = "Vancouver International Airport",
                arrivalIataCode = "YWG",
                arrivalAirportName = "Winnipeg International Airport",
                isFavorite = false
            ),

            // Toronto departures
            FlightDetails(
                departureIataCode = "YYZ",
                departureAirportName = "Toronto International Airport",
                arrivalIataCode = "YYC",
                arrivalAirportName = "Calgary International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YYZ",
                departureAirportName = "Toronto International Airport",
                arrivalIataCode = "YEG",
                arrivalAirportName = "Edmonton International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YYZ",
                departureAirportName = "Toronto International Airport",
                arrivalIataCode = "YVR",
                arrivalAirportName = "Vancouver International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YYZ",
                departureAirportName = "Toronto International Airport",
                arrivalIataCode = "YWG",
                arrivalAirportName = "Winnipeg International Airport",
                isFavorite = false
            ),

            // Winnipeg departures
            FlightDetails(
                departureIataCode = "YWG",
                departureAirportName = "Toronto International Airport",
                arrivalIataCode = "YYC",
                arrivalAirportName = "Calgary International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YWG",
                departureAirportName = "Toronto International Airport",
                arrivalIataCode = "YEG",
                arrivalAirportName = "Edmonton International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YWG",
                departureAirportName = "Toronto International Airport",
                arrivalIataCode = "YVR",
                arrivalAirportName = "Vancouver International Airport",
                isFavorite = false
            ),
            FlightDetails(
                departureIataCode = "YWG",
                departureAirportName = "Toronto International Airport",
                arrivalIataCode = "YYZ",
                arrivalAirportName = "Toronto International Airport",
                isFavorite = false
            )
        )
    }
}
