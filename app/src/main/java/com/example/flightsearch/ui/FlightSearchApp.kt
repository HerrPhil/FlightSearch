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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.R
import com.example.flightsearch.data.InterimAirportDataProvider
import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.domain.FlightDetails
import com.example.flightsearch.ui.screens.FlightsViewModel
import com.example.flightsearch.ui.screens.HomeScreen
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun FlightSearchApp(
//    viewModel: FlightsViewModel = viewModel( factory = FlightsViewModel.factory)
) {

    // Add some scroll behavior to the top app bar
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Create a scaffold to contain the screen
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { FlightSearchTopAppBar(scrollBehavior = scrollBehavior) },
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            //
            // TODO access the view model and ui state here
            //

            // TODO replace with view model eventually - represents current state of dropdown UI = ui state
            val temporaryAirportDropdownExpandedUiState = rememberSaveable { mutableStateOf(false) }

            // TODO replace with view model eventually - transferred to view model
            val toggleAirportDropdown: (Boolean) -> Unit = { expanded ->
                temporaryAirportDropdownExpandedUiState.value = expanded
                Log.i(
                    "FilteredSearch",
                    "UI State says expanded state after is ${temporaryAirportDropdownExpandedUiState.value}"
                )
            }

            // TODO replace with view model eventually - transferred to view model
            val collapseAirportDropdown: () -> Unit = {
                temporaryAirportDropdownExpandedUiState.value = false
            }


            // TODO replace with view model eventually - represents current state of textfield UI = ui state
            val temporarySearchValueUiState = rememberSaveable { mutableStateOf("") }

            val AirportDetailsSaver = listSaver(
                save = {
                    listOf<Any>(it.value.id, it.value.iataCode, it.value.name, it.value.passengers)
                },
                restore = { data ->
                    mutableStateOf(
                        AirportDetails(
                            id = data[0] as Int,
                            iataCode = data[1] as String,
                            name = data[2] as String,
                            passengers = data[3] as Int
                        )
                    )
                }
            )
            // TODO replace with view model eventually - represents current state of selected departure airport UI = ui state
            val temporaryDepartureValueUiState = rememberSaveable(
                saver = AirportDetailsSaver
            ) {
                mutableStateOf(AirportDetails(0, "", "", 0))
//                mutableStateOf(InterimAirportDataProvider.defaultNothingAirport)
            }

            // TODO replace with view model eventually - represents business logic of when to show possible flights
            val temporaryShowPossibleFlightsUiState = rememberSaveable { mutableStateOf(false) }

            // TODO replace with view model eventually - represents business logic of when to show favorite flights
            val temporaryShowFavoritesUiState = rememberSaveable { mutableStateOf(false) }

            // TODO replace with view model eventually - represents repository call
            val temporaryDepartureAirportOptionsDataSource = InterimAirportDataProvider.airports

            // TODO replace with view model eventually - represents repository call
            val temporaryFlightsDataSource = InterimAirportDataProvider.flights

            // TODO replace with view model eventually - represents current state of departure airport options UI = ui state.
            //      Transferred.
            val temporaryFilteredOptions = temporaryDepartureAirportOptionsDataSource.filter { airportDetails ->
                temporarySearchValueUiState.value.isNotBlank() &&
                        (airportDetails.name.contains(
                            other = temporarySearchValueUiState.value,
                            ignoreCase = true
                        ) || airportDetails.iataCode.contains(
                            other = temporarySearchValueUiState.value,
                            ignoreCase = true
                        ))
            }

            // TODO replace with view model eventually - represents business logic of when to show favorite flights
            val temporaryPossibleFlights: SnapshotStateList<FlightDetails> = rememberSaveable(
                saver = listSaver(
                    save = { it.toList() },
                    restore = { it.toMutableStateList() }
                )
            ) {
                mutableStateListOf() // initial value is EMPTY
            }

            // TODO replace with view model eventually - represents business logic of when to show favorite flights
            val temporaryFavoriteFlights: SnapshotStateList<FlightDetails> = rememberSaveable(
                saver = listSaver(
                    save = { it.toList() },
                    restore = { it.toMutableStateList() }
                )
            ) {
                mutableStateListOf() // initial value is EMPTY
            }

            // TODO replace with view model eventually
            val noFlights: SnapshotStateList<FlightDetails> = rememberSaveable(
                saver = listSaver(
                    save = { it.toList() },
                    restore = { it.toMutableStateList() }
                )
            ) {
                mutableStateListOf() // initial value is EMPTY
            }


            // TODO replace with view model eventually - represents business logic of when to show favorite flights.
            //      Transferred.
            temporaryShowFavoritesUiState.value = validateFavoritesUiState(
                temporarySearchValueUiState.value.isBlank(),
                temporaryFavoriteFlights.isNotEmpty()
            )

            // TODO replace with view model eventually
            // Transferred update of search value only
            val onSearchValueChange: (String) -> Unit = {
                Log.i("FilteredSearch", "FlightSearchApp The current search value to remember: $it")
                temporarySearchValueUiState.value = it
                if (it.isBlank()) {
                    temporaryShowPossibleFlightsUiState.value = false
                }

                temporaryShowFavoritesUiState.value = validateFavoritesUiState(
                    it.isBlank(),
                    temporaryFavoriteFlights.isNotEmpty()
                )

            }

            val onSetDepartureSelection: (AirportDetails) -> Unit = {
                Log.i(
                    "FilteredSearch",
                    "FlightSearchApp In set departure selection, the selected departure to remember: $it"
                )
                temporaryDepartureValueUiState.value = it

                // TODO when the airport details domain object is created,
                //      then set the search value to the airport code (ie. YYC).
                //      for now, use the departure string.
                temporarySearchValueUiState.value = temporaryDepartureValueUiState.value.iataCode

                // When the departure is selected, then get the possible flights for it.
                // Will schedule recomposition - is REMEMBERED and MUTABLE.
                Log.i(
                    "FilteredSearch",
                    "FlightSearchApp in set departure selection, The app will display a list of flights."
                )
                temporaryShowPossibleFlightsUiState.value = true
                // TODO call repo/db to GET flights using selected departure
                // TODO get fake Flight list
                temporaryPossibleFlights.clear()
//                temporaryPossibleFlights.addAll(mutableListOf("flight 9", "flight 8", "flight 7"))
                temporaryPossibleFlights.addAll(temporaryFlightsDataSource)
                Log.i(
                    "FilteredSearch",
                    "FlightSearchApp in set departure selection, temporary flights list is:"
                )
                Log.i("FilteredSearch", temporaryPossibleFlights.toString())
            }

            // TODO replace with view model eventually
            val onToggleFavorites: (FlightDetails) -> Unit = { favoriteFlight ->
                Log.i(
                    "FilteredSearch",
                    "The toggle favorite flight for departure ${favoriteFlight.departureIataCode} and arrival ${favoriteFlight.arrivalIataCode}"
                )
                if (temporaryFavoriteFlights.contains(favoriteFlight)) {
                    Log.i("FilteredSearch", "The event toggle favorites removes flight")
                    if (temporaryFavoriteFlights.size == 1) {
                        temporaryFavoriteFlights.clear()
                    } else {
                        val temp = temporaryFavoriteFlights.filter {
                            it != favoriteFlight
                        }
                        temporaryFavoriteFlights.clear()
                        temporaryFavoriteFlights.addAll(temp)

                    }
                } else { // does not contain flight
                    Log.i("FilteredSearch", "The event toggle favorites adds flight")
                    temporaryFavoriteFlights.add(favoriteFlight)
                }
            }

            // TODO replace with view model eventually - represents current state of flights (possible or favorites) UI = ui state
            val displayFlights =
                if (temporaryShowFavoritesUiState.value) {
                    Log.i(
                        "FilteredSearch",
                        "FlightSearchApp in set display flights, assign favorite flights"
                    )
                    temporaryFavoriteFlights
                } else if (temporaryShowPossibleFlightsUiState.value) {
                    Log.i(
                        "FilteredSearch",
                        "FlightSearchApp in set display flights, assign possible flights"
                    )
                    temporaryPossibleFlights
                } else {
                    Log.i(
                        "FilteredSearch",
                        "FlightSearchApp in set display flights, otherwise assign no flights"
                    )
                    noFlights
                }

            val resultsLabel: String =
                if (temporaryShowFavoritesUiState.value) {
                    "Favorite routes"
                } else if (temporaryShowPossibleFlightsUiState.value) {
                    "Flights from ${temporaryDepartureValueUiState.value.iataCode}"
                } else {
                    ""
                }

            // TODO in the fullness of time, re-factor state values to UI State class
            // TODO in the fullness of time, re-factor these to the view model


            // TODO pass ui state to home screen
            // TODO pass event actions to home screen
            // TODO pass temporary flight list; eventually pass list of domain objects

            Log.i("FilteredSearch", "FlightSearchApp Call HomeScreen")
            HomeScreen(
                airportDropdownExpanded = temporaryAirportDropdownExpandedUiState.value,
                searchValue = temporarySearchValueUiState.value,
                searchOptions = temporaryFilteredOptions,
                resultsLabel = resultsLabel,
                flights = displayFlights, // the display flights - might be possible or favorite list
                toggleAirportDropdown = toggleAirportDropdown,
                collapseAirportDropdown = collapseAirportDropdown,
                onSearchValueChange = onSearchValueChange,
                onSetDepartureSelection = onSetDepartureSelection,
                onToggleFavorites = onToggleFavorites,
                contentPadding = it
            )

        }
    }

}

@Composable
fun FlightSearchTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
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
fun FlightSearchAppPreview() {
    FlightSearchTheme {
        FlightSearchApp()
    }
}

// The business rule for favorites; here it is.
// When the user clears the search value, and there exists favorite flights,
// then show the favorite flights.
private fun validateFavoritesUiState(searchValueIsBlank: Boolean, flightsExist: Boolean): Boolean {
    return searchValueIsBlank && flightsExist
}
