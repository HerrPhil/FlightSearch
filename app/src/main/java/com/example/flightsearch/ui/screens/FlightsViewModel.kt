package com.example.flightsearch.ui.screens

import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class FlightsViewModel : ViewModel() {

    // TODO move all the UI State from the app composable function to the view model and its ui state

    private val _uiState = MutableStateFlow(
        "" // temporary - allow build until re-factoring is COMPLETE
//        FlightsUiState(
//
//        )
    )
}