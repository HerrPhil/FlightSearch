package com.example.flightsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.flightsearch.ui.FlightSearchApp
import com.example.flightsearch.ui.OldFlightSearchApp
import com.example.flightsearch.ui.theme.FlightSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlightSearchTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // minimize logic in main activity
                    FlightSearchApp()
                }
            }
        }
    }
}
