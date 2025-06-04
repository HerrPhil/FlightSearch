package com.example.flightsearch.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.data.InterimAirportDataProvider
import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.domain.FlightDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FlightsViewModel : ViewModel() {

    private val defaultNothingAirportDetails = AirportDetails(0, "", "", 0)

    private val _uiState = MutableStateFlow(
        FlightsUiState(
            airportDropdownExpanded = false,
            searchValue = "",
            departureAirportDetails = defaultNothingAirportDetails,
        )
    )
    val uiState: StateFlow<FlightsUiState> = _uiState


    fun toggleAirportDropdown(expanded: Boolean) {
        _uiState.update {
            it.copy(
                airportDropdownExpanded = expanded
            )
        }
    }

    fun collapseAirportDropdown() {
        _uiState.update {
            it.copy(
                airportDropdownExpanded = false
            )
        }
    }

    fun updateSearchValue(newValue: String) {
        _uiState.update {
            it.copy(
                searchValue = newValue
            )
        }
    }

    fun updateDepartureDetails(newAirportDetails: AirportDetails) {
        _uiState.update {
            it.copy(
                searchValue = newAirportDetails.iataCode,
                departureAirportDetails = newAirportDetails,
            )
        }
    }

    fun getAirportOptions(): Flow<List<AirportDetails>> = InterimAirportDataProvider.getAirports()
            .map { airports: List<AirportDetails> ->
                airports.filter { airportDetails: AirportDetails ->

                    val searchValue = _uiState.value.searchValue

                    searchValue.isNotBlank() &&
                            (airportDetails.name.contains(
                                other = searchValue,
                                ignoreCase = true
                            ) || airportDetails.iataCode.contains(
                                other = searchValue,
                                ignoreCase = true
                            ))

                }
            }

    fun getIataOfDepartureAirportDetails(): String = _uiState.value.departureAirportDetails.iataCode

    fun getPossibleFlightsFor(iataCode: String): Flow<List<FlightDetails>> =
        InterimAirportDataProvider.getPossibleFlights(iataCode)

    fun getPossibleFlightsForxxx(iataCode: String = _uiState.value.departureAirportDetails.iataCode): Flow<List<FlightDetails>> =
        InterimAirportDataProvider.getPossibleFlights(iataCode)

    fun getFavoriteFlights(): Flow<List<FlightDetails>> =
        InterimAirportDataProvider.getFavoriteFlights()

    fun isFavoriteFlightsDisplayed(): Boolean {
        return with(_uiState.value) {
            searchValue.isBlank()
        }
    }

    fun isPossibleFlightsDisplayed(): Boolean {
        return with(_uiState.value) {
            defaultNothingAirportDetails != departureAirportDetails &&
                    departureAirportDetails.iataCode.equals(searchValue, ignoreCase = true)
        }
    }

    suspend fun addFavorite(flightDetails: FlightDetails) {
        InterimAirportDataProvider.addFavorite(flightDetails)
    }

    suspend fun removeFavorite(flightDetails: FlightDetails) {
        InterimAirportDataProvider.removeFavorite(flightDetails)
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FlightsViewModel()
            }
        }
    }

}

data class FlightsUiState(
    val airportDropdownExpanded: Boolean,
    val searchValue: String,
    val departureAirportDetails: AirportDetails
)
