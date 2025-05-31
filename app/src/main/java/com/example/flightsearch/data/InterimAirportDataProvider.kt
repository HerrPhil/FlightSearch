package com.example.flightsearch.data

import com.example.flightsearch.domain.AirportDetails

object InterimAirportDataProvider {

    val airports = getInterimAirportDetails()

    val defaultNothingAirport = AirportDetails(0, "", "", 0)

    private fun getInterimAirportDetails():List<AirportDetails> {
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
}