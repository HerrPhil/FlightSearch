package com.example.flightsearch.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.data.InterimAirportDataProvider
import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.domain.FlightDetails
import com.example.flightsearch.ui.components.AutoCompleteSearchTextField
import com.example.flightsearch.ui.components.FlightResults
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun HomeScreen(
    searchValue: String,
    searchOptions: List<AirportDetails>,
    resultsLabel: String,
    flights: List<FlightDetails>,
    onSearchValueChange: (String) -> Unit,
    onSetDepartureSelection: (AirportDetails) -> Unit,
    onToggleFavorites: (FlightDetails) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    Log.i("FilteredSearch", "HomeScreen beginning of function")

    Column(
        modifier.padding(top = contentPadding.calculateTopPadding())
    ) {

        // TODO get list of options from ui state
        // TODO pass ui state to home screen

        Log.i("FilteredSearch", "Call AutoCompleteSearchTextField")
        AutoCompleteSearchTextField(
            searchValue,
            onSearchValueChange,
            onSetDepartureSelection,
            searchOptions,
            modifier.padding(16.dp)
        )

        Log.i("FilteredSearch", "check if flights not empty")
        if (flights.isNotEmpty()) {
            Log.i("FilteredSearch", "found flight results to display")
            FlightResults(resultsLabel, flights, onToggleFavorites, contentPadding = contentPadding)
        } else {
            Log.i("FilteredSearch", "found no flight results to display")
        }

    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FlightSearchTheme {
        HomeScreen(
            searchValue = "YYC",
            searchOptions = InterimAirportDataProvider.airports,
            flights = InterimAirportDataProvider.flights,
            resultsLabel = "Flights from YYC",
            onSearchValueChange = {},
            onSetDepartureSelection = {},
            onToggleFavorites = {}
        )
    }
}
