@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.flightsearch.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.domain.FlightDetails
import com.example.flightsearch.ui.screens.FlightsViewModel
import com.example.flightsearch.ui.screens.HomeScreen
import com.example.flightsearch.ui.theme.FlightSearchTheme
import kotlinx.coroutines.launch

@Composable
fun FlightSearchApp(
    viewModel: FlightsViewModel = viewModel(factory = FlightsViewModel.factory)
) {

    Log.i("uistate", "FlightSearchAppModel the beginning of app composable")
    // Add some scroll behavior to the top app bar
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Reference the UI state values of the view model
    val flightSearchUiState by viewModel.flightSearchUiState.collectAsState()
    Log.i(
        "uistate",
        "FlightSearchAppModel the ui state search value = <<${flightSearchUiState.searchValue}>>"
    )

    Log.i("uistate", "expect new airport details because of user search value")
    val airportResultsUiState by viewModel.displayAirportDetailsUiState.collectAsState()
    Log.i(
        "uistate",
        "FlightSearchAppModel size of get filtered airport options = ${airportResultsUiState.airportDetailsList.size}"
    )

    Log.i("uistate", "expect new airport details from db because of user search value")
    val airportResultsUiStatezzz by viewModel.displayAirportDetailsUiStatezzz.collectAsState()
    Log.i(
        "uistate",
        "FlightSearchAppModel size of get db filtered airport options = ${airportResultsUiStatezzz.airportDetailsList.size}"
    )

    Log.i("uistate", "expect new airport details because of user airport details selection")
    val flightResultsUiState by viewModel.displayFlightDetailsUiState.collectAsState()
    Log.i(
        "uistate",
        "FlightSearchAppModel size of get flight details of selected airport option = ${flightResultsUiState.flightDetailsList.size}"
    )

    val coroutineScope = rememberCoroutineScope()

    // Create a scaffold to contain the screen
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { FlightSearchModelTopAppBar(scrollBehavior = scrollBehavior) },
    ) {

        Log.i("uistate", "FlightSearchAppModel the beginning of scaffold composable")

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            Log.i("uistate", "FlightSearchAppModel the beginning of surface composable")

            val toggleAirportDropdownByViewModel: (Boolean) -> Unit = { expanded ->
                Log.i(
                    "uistate",
                    "FlightSearchAppModel click event toggleAirportDropdownByViewModel($expanded)"
                )
                viewModel.toggleAirportDropdown(expanded)
            }

            val collapseAirportDropdownByViewModel: () -> Unit = {
                Log.i(
                    "uistate",
                    "FlightSearchAppModel click event collapseAirportDropdownByViewModel()"
                )
                viewModel.toggleAirportDropdown(false) // false = collapse dropdown
            }

            val onSearchValueChangedByViewModel: (String) -> Unit = {
                Log.i(
                    "uistate",
                    "FlightSearchAppModel click event onSearchValueChangedByViewModel($it)"
                )
                viewModel.updateSearchValue(it)
            }

            val onSetDepartureSelectionByViewModel: (AirportDetails) -> Unit = {
                Log.i(
                    "uistate",
                    "FlightSearchAppModel click event onSetDepartureSelectionByViewModel($it)"
                )
                viewModel.updateDepartureDetails(it)
            }

            val onToggleFavoritesByViewModel: (FlightDetails) -> Unit = { favoriteFlight ->
                Log.i(
                    "uistate",
                    "FlightSearchAppModel click event onToggleFavoritesByViewModel(${favoriteFlight.departureIataCode}, ${favoriteFlight.arrivalIataCode})"
                )
                if (viewModel.exists(favoriteFlight)) {
                    Log.i(
                        "uistate",
                        "FlightSearchAppModel click event onToggleFavoritesByViewModel remove this flight"
                    )
                    val job = coroutineScope.launch {
                        viewModel.remove(favoriteFlight)
                    }
                    coroutineScope.launch {
                        job.join()
                        viewModel.updateFavoritesList()
                    }
                } else {
                    Log.i(
                        "uistate",
                        "FlightSearchAppModel click event onToggleFavoritesByViewModel add this flight"
                    )
                    coroutineScope.launch {
                        viewModel.add(favoriteFlight)
                    }
                }
            }

            Log.i("uistate", "FlightSearchAppModel Call HomeScreen to show ${flightResultsUiState.flightDetailsList} records")
            HomeScreen(
                airportDropdownExpanded = flightSearchUiState.airportDropdownExpanded,
                searchValue = flightSearchUiState.searchValue,
                searchOptions = airportResultsUiStatezzz.airportDetailsList,
//                searchOptions = airportResultsUiState.airportDetailsList,
                resultsLabel = viewModel.getLabel(),
                flights = flightResultsUiState.flightDetailsList, // the display flights - might be possible or favorite list
                toggleAirportDropdown = toggleAirportDropdownByViewModel,
                collapseAirportDropdown = collapseAirportDropdownByViewModel,
                onSearchValueChange = onSearchValueChangedByViewModel,
                onSetDepartureSelection = onSetDepartureSelectionByViewModel,
                onToggleFavorites = onToggleFavoritesByViewModel,
                contentPadding = it
            )

        }
    }

}

@Composable
fun FlightSearchModelTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.surface,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun FlightSearchAppModelPreview() {
    FlightSearchTheme {
        OldFlightSearchApp()
    }
}
