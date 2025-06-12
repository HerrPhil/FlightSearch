package com.example.flightsearch.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.data.PreviewAirportDataProvider
import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.domain.FlightDetails
import com.example.flightsearch.ui.components.AutoCompleteSearchTextField
import com.example.flightsearch.ui.components.FlightResults
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun HomeScreen(
    flightSearchUiState: FlightSearchUiState,
    airportResultsUiState: AirportResultsUiState,
    resultsLabel: String,
    flightResultsUiState: FlightResultsUiState,
    toggleAirportDropdown: (Boolean) -> Unit,
    collapseAirportDropdown: () -> Unit,
    onSearchValueChange: (String) -> Unit,
    onSetDepartureSelection: (AirportDetails) -> Unit,
    onToggleFavorites: (FlightDetails) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    Log.i("uistate", "HomeScreen beginning of function")

    Column(
        modifier.padding(top = contentPadding.calculateTopPadding())
    ) {

        // TODO get list of options from ui state
        // TODO pass ui state to home screen

        Log.i("uistate", "Call AutoCompleteSearchTextField")
        AutoCompleteSearchTextField(
            flightSearchUiState,
            airportResultsUiState,
            toggleAirportDropdown,
            collapseAirportDropdown,
            onSearchValueChange,
            onSetDepartureSelection,
            modifier.padding(16.dp)
        )

        Log.i("uistate", "check if flights not empty")
        val hasFlights = flightResultsUiState.flightDetailsList.isNotEmpty()
        if (hasFlights) {
            Log.i("uistate", "found flight results to display")
            FlightResults(resultsLabel, flightResultsUiState, onToggleFavorites, contentPadding = contentPadding)
        } else {
            Log.i("uistate", "found no flight results to display")
        }

    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FlightSearchTheme {
        HomeScreen(
            FlightSearchUiState(),
            AirportResultsUiState(),
            resultsLabel = "Flights from YYC",
            FlightResultsUiState(),
            toggleAirportDropdown = {},
            collapseAirportDropdown = {},
            onSearchValueChange = {},
            onSetDepartureSelection = {},
            onToggleFavorites = {}
        )
    }
}
