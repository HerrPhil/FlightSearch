package com.example.flightsearch.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.ui.components.AutoCompleteSearchTextField
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    Log.i("FilteredSearch", "In HomeScreen")
    val temporarySearchValueUiState = remember { mutableStateOf("") }
//    val temporarySearchValueUiState = remember { mutableStateOf("air") }

    Column(
        modifier.padding(top = contentPadding.calculateTopPadding())
    ) {
//        Text(text = "TODO - add the rest of the app here")

        // TODO implement the search results text field drop down filtering

        // TODO get list of options from ui state
        // TODO pass ui state to home screen
        val searchValue = temporarySearchValueUiState.value
        val options = listOf(
            "YYC calgary international airport",
            "YEG edmonton international airport",
            "YVR vancouver international airport",
            "YYZ toronto international airport",
            "YWG winnipeg international airport"
        )
        Log.i("FilteredSearch", "Call AutoCompleteSearchTextField")
        AutoCompleteSearchTextField(
            searchValue,
            {
                Log.i("FilteredSearch", "The current search value to remember: $it")
                temporarySearchValueUiState.value = it
            },
            options
        )

    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FlightSearchTheme {
        HomeScreen()
    }
}
