package com.example.flightsearch.data

import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.domain.FlightDetails

object PreviewAirportDataProvider {

    val airports = getInterimAirportDetails()

    val flightDetails = getInterimFlightDetails()

    private fun getInterimAirportDetails(): List<AirportDetails> {
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
        return listOf(
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
        )
    }
}
