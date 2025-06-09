package com.example.flightsearch.domain

import java.util.UUID

// The flight ID only exist to have a key value for the items in the LazyColumn.
data class FlightDetails(
    val flightID: String = UUID.randomUUID().toString(),
    val departureIataCode: String,
    val departureAirportName: String,
    val arrivalIataCode: String,
    val arrivalAirportName: String,
) {
    var favorite: Boolean = false
}
