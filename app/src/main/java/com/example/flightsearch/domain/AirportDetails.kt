package com.example.flightsearch.domain

data class AirportDetails(
    val id: Int,
    val iataCode: String,
    val name: String,
    val passengers: Int
)
