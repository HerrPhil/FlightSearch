package com.example.flightsearch.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.ui.components.AutoCompleteSearchTextField
import com.example.flightsearch.ui.components.FlightResults
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun HomeScreen(
    searchValue: String,
    showFlights: Boolean,
    showFavorites: Boolean,
    searchOptions: List<String>,
    flights: List<String>,
    onSearchValueChange: (String) -> Unit,
    onGetFlights: () -> Unit,
    onToggleFavorites: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    Log.i("FilteredSearch", "In HomeScreen")

    Column(
        modifier.padding(top = contentPadding.calculateTopPadding())
    ) {
//        Text(text = "TODO - add the rest of the app here")

        // TODO implement the search results text field drop down filtering

        // TODO get list of options from ui state
        // TODO pass ui state to home screen

        Log.i("FilteredSearch", "Call AutoCompleteSearchTextField")
        AutoCompleteSearchTextField(
            searchValue,
            onSearchValueChange,
            onGetFlights,
            searchOptions,
            modifier.padding(16.dp)
        )

        if (showFlights) {
//            Text(text = "TODO show the flights from ${searchValue}")
            FlightResults(searchValue, flights, onToggleFavorites, contentPadding = contentPadding)
        }

        if (showFavorites) {
            Log.i("FilteredSearch", "TODO show the favorites")
        } else {
            Log.i("FilteredSearch", "TODO hide the favorites")
        }

    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FlightSearchTheme {
        HomeScreen(
            searchValue = "YYC",
            showFlights = true,
            showFavorites = false,
            searchOptions = listOf("yyc calgary", "yeg edmonton", "ywg winnipeg"),
            flights = listOf("flight 1", "flight 2", "flight 3", "flight 4"),
            onSearchValueChange = {},
            onGetFlights = {},
            onToggleFavorites = {}
        )
    }
}
