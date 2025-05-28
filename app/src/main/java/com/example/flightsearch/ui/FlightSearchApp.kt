@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.flightsearch.ui

import android.util.Log
import androidx.compose.foundation.background
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
        topBar = { FlightSearchTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            // TODO access the view model and ui state here

            // TODO pass ui state to home screen
            // TODO pass event actions to home screen
            HomeScreen(
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
