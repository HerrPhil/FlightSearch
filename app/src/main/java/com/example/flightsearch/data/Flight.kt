package com.example.flightsearch.data

// This data class is not a table Entity.
// It is a query response.
data class Flight(
    val departureIataCode: String,
    val departureAirportName: String,
    val arrivalIataCode: String,
    val arrivalAirportName: String,
    val isFavorite: Boolean
)
