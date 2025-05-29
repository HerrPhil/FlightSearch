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
import com.example.flightsearch.R
import com.example.flightsearch.ui.screens.HomeScreen
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun FlightSearchApp() {

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
            // TODO access the view model and ui state here

            // TODO re-factor state and event actions here from HomeScreen
            val temporarySearchValueUiState = rememberSaveable { mutableStateOf("") }
            val temporaryShowFlightsUiState = rememberSaveable { mutableStateOf(false) }
            val temporaryShowFavoritesUiState = rememberSaveable { mutableStateOf(false) }

            // TODO replace with view model eventually
            val temporaryOptionsDataSource = listOf(
                "YYC calgary international airport",
                "YEG edmonton international airport",
                "YVR vancouver international airport",
                "YYZ toronto international airport",
                "YWG winnipeg international airport"
            )

            // TODO replace with view model eventually
            // TODO replace with airport detail domain objects
            val temporaryFilteredOptions = temporaryOptionsDataSource.filter {
                temporarySearchValueUiState.value.isNotBlank() &&
                        it.contains(
                            other = temporarySearchValueUiState.value,
                            ignoreCase = true
                        )
            }

            // TODO replace with view model eventually
            // TODO replace with flight detail domain objects
            var temporaryFlightList = listOf(
                "Flight 1",
                "Flight 2",
                "Flight 3",
                "Flight 4",
                "Flight 5",
                "Flight 6",
                "Flight 7",
                "Flight 8",
                "Flight 9",
                "Flight 10",
                "Flight 11",
            )

            val temporaryFavoriteFlights: SnapshotStateList<String> = rememberSaveable(
                saver = listSaver(
                    save = { it.toList() },
                    restore = { it.toMutableStateList() }
                )
            ) {
                mutableStateListOf()
//                mutableStateListOf("favorite flight 1")
            }

            // TODO replace with view model eventually - initialization
            temporaryShowFavoritesUiState.value = validateFavoritesUiState(temporarySearchValueUiState.value, temporaryFavoriteFlights.isNotEmpty())

            // TODO replace with view model eventually
            val onSearchValueChange: (String) -> Unit = {
                Log.i("FilteredSearch", "The current search value to remember: $it")
                temporarySearchValueUiState.value = it
                if (it.isBlank()) {
                    temporaryShowFlightsUiState.value = false
                }

                // The business rule for favorites; here it is.
                // When the user clears the search value, and there exists favorite flights,
                // then show the favorite flights; the header text is "Favorite routes"
                temporaryShowFavoritesUiState.value =
                    it.isBlank() && temporaryFavoriteFlights.isNotEmpty()
                temporaryShowFavoritesUiState.value = validateFavoritesUiState(it, temporaryFavoriteFlights.isNotEmpty())

            }


            // TODO replace with view model eventually
            val onGetFlights: () -> Unit = {
                Log.i("FilteredSearch", "The app will display a list of flights.")
                temporaryShowFlightsUiState.value = true
                // TODO get fake Flight list
                temporaryFlightList = listOf("flight 1", "flight 2", "flight 3")
            }

            // TODO replace with view model eventually
            val onToggleFavorites: (String) -> Unit = { favoriteFlight ->
                Log.i("FilteredSearch", "The event toggles favorites: $favoriteFlight")
                if (temporaryFavoriteFlights.contains(favoriteFlight)) {
                    val temp = temporaryFavoriteFlights.filter {
                        it != favoriteFlight
                    }
                    temporaryFavoriteFlights.clear()
                    temporaryFavoriteFlights.addAll(temp)
                }
                if (!temporaryFavoriteFlights.contains(favoriteFlight)) {
                    temporaryFavoriteFlights.add(favoriteFlight)
                }
            }

            // TODO in the fullness of time, re-factor state values to UI State class
            // TODO in the fullness of time, re-factor these to the view model


            // TODO pass ui state to home screen
            // TODO pass event actions to home screen
            // TODO pass temporary flight list; eventually pass list of domain objects
            HomeScreen(
                searchValue = temporarySearchValueUiState.value,
                showFlights = temporaryShowFlightsUiState.value,
                showFavorites = temporaryShowFavoritesUiState.value,
                searchOptions = temporaryFilteredOptions, // temporaryOptions.toList(),
                flights = temporaryFlightList,
                onSearchValueChange = onSearchValueChange,
                onGetFlights = onGetFlights,
                onToggleFavorites = onToggleFavorites,
                contentPadding = it
            )
        }
    }

}

fun validateFavoritesUiState(searchValue: String, flightsExist: Boolean): Boolean {
    return searchValue.isBlank() && flightsExist
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
