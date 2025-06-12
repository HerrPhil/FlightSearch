package com.example.flightsearch.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.Flight
import com.example.flightsearch.data.FlightSearchRepository
import com.example.flightsearch.data.InterimAirportDataProvider
import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.domain.FlightDetails
import com.example.flightsearch.domain.FlightResultsType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightsViewModel(private val flightSearchRepository: FlightSearchRepository) : ViewModel() {

    private val _flightSearchUiState = MutableStateFlow(
        FlightSearchUiState()
    )
    val flightSearchUiState: StateFlow<FlightSearchUiState> = _flightSearchUiState

    init {
        Log.i("uistate", "initialized FlightViewModel")
    }

    var displayAirportDetailsUiState = getAirportDetailsStateFlow()

    var displayAirportDetailsUiStatezzz = getAirportDetailsStateFlowzzz()

    var displayFlightDetailsUiState = getDisplayFlightsStateFlow()

    fun getLabel(): String = with(_flightSearchUiState.value) {
        when (flightResultsType) {
            FlightResultsType.FAVORITE_FLIGHTS -> favoritesLabel
            FlightResultsType.POSSIBLE_FLIGHTS -> resultsLabel
            FlightResultsType.NONE -> noResultsLabel
        }
    }

    fun toggleAirportDropdown(expanded: Boolean) {
        Log.i("uistate", "start FlightsViewModel toggleAirportDropdown($expanded)")
        _flightSearchUiState.update {
            it.copy(
                airportDropdownExpanded = expanded
            )
        }
    }

    fun updateSearchValue(newValue: String) {
        Log.i("uistate", "start FlightsViewModel updateSearchValue($newValue)")
        val favoriteFlightsCount = InterimAirportDataProvider.getFavoriteFlightsCount()
        _flightSearchUiState.update {
            it.copy(
                searchValue = newValue,
                flightResultsType = getFlightResultsType(newValue), // dependent on new search value for favorite flights check
                favoritesLabel = "Favorite routes ($favoriteFlightsCount)"
            )
        }
        displayAirportDetailsUiState = getAirportDetailsStateFlow()
        displayAirportDetailsUiStatezzz = getAirportDetailsStateFlowzzz()
        displayFlightDetailsUiState = getDisplayFlightsStateFlow()
    }

    fun updateFavoritesList() {
        if (_flightSearchUiState.value.flightResultsType != FlightResultsType.FAVORITE_FLIGHTS) return;
        Log.i("uistate", "start FlightsViewModel updateFavoritesList()")
        val favoriteFlightsCount = InterimAirportDataProvider.getFavoriteFlightsCount()
        val updatedFlightResultsType = getFlightResultsType("")
        val updatedSearchValue =
            if (updatedFlightResultsType == FlightResultsType.POSSIBLE_FLIGHTS) {
                _flightSearchUiState.value.departureAirportDetails.iataCode
            } else {
                ""
            }
        _flightSearchUiState.update {
            it.copy(
                searchValue = updatedSearchValue,
                flightResultsType = updatedFlightResultsType, // dependent on new search value for favorite flights check
                favoritesLabel = "Favorite routes ($favoriteFlightsCount)"
            )
        }
        displayFlightDetailsUiState = getDisplayFlightsStateFlow()
    }

    fun updateDepartureDetails(newAirportDetails: AirportDetails) {
        Log.i(
            "uistate",
            "start FlightsViewModel updateDepartureDetails(${newAirportDetails.iataCode})"
        )
        _flightSearchUiState.update {
            it.copy(
                searchValue = newAirportDetails.iataCode,
                departureAirportDetails = newAirportDetails,
                flightResultsType = FlightResultsType.POSSIBLE_FLIGHTS,
                resultsLabel = "Flights from ${newAirportDetails.iataCode}"
            )
        }
        displayFlightDetailsUiState = getDisplayFlightsStateFlow()
    }

    fun exists(favoriteFlightDetails: FlightDetails): Boolean =
        InterimAirportDataProvider.exists(favoriteFlightDetails)

    suspend fun add(favoriteFlightDetails: FlightDetails) {
        Log.i(
            "uistate",
            "start add favorite flight (${favoriteFlightDetails.departureIataCode}, ${favoriteFlightDetails.arrivalIataCode})"
        )
        InterimAirportDataProvider.addFavorite(favoriteFlightDetails)
    }

    suspend fun remove(favoriteFlightDetails: FlightDetails) {
        Log.i(
            "uistate",
            "start remove favorite flight (${favoriteFlightDetails.departureIataCode}, ${favoriteFlightDetails.arrivalIataCode})"
        )
        InterimAirportDataProvider.removeFavorite(favoriteFlightDetails)
    }

    private fun getAirportDetailsStateFlow(): StateFlow<AirportResultsUiState> =
        getAirportDetails().map { AirportResultsUiState(it) }
            .filter {
                Log.i("uistate", "created AirportResultsUiState of list of airports")
                true
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AirportResultsUiState()
            )

    private fun getAirportDetailsStateFlowzzz(): StateFlow<AirportResultsUiState> =
        getAirportDetailzzz()
            .mapNotNull {
                it.map {airport ->
                    airport.toAirportDetails()
                }
            }
            .map { AirportResultsUiState(it) }
            .filter {
                Log.i("uistate", "created AirportResultsUiState of list of airports")
                true
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AirportResultsUiState()
            )

    private fun getAirportDetails(): Flow<List<AirportDetails>> =
        with(_flightSearchUiState.value) {
            return InterimAirportDataProvider.getAirportsBy(searchValue)
        }

    private fun getAirportDetailzzz(): Flow<List<Airport>> =
        with(_flightSearchUiState.value) {
            if (searchValue.isBlank()) return emptyFlow()
            return flightSearchRepository.getAirportsBy("%$searchValue%")
        }

    private fun getDisplayFlightsStateFlow(): StateFlow<FlightResultsUiState> =
        getDisplayFlights()
            .map { FlightResultsUiState(it) }
            .filter {
                Log.i("uistate", "created newer FlightResultsUiState of list of flights")
                true
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FlightResultsUiState()
            )

    private fun getDisplayFlights(): Flow<List<FlightDetails>> = with(_flightSearchUiState.value) {
        when (flightResultsType) {
            FlightResultsType.FAVORITE_FLIGHTS -> getFavoriteFlights()
            FlightResultsType.POSSIBLE_FLIGHTS -> getPossibleFlightsByDepartureAirportzzz()
//            FlightResultsType.POSSIBLE_FLIGHTS -> getPossibleFlightsByDepartureAirport()
            FlightResultsType.NONE -> flow { emptyList<FlightDetails>() }
        }
    }

    private fun getFavoriteFlights(): Flow<List<FlightDetails>> =
        InterimAirportDataProvider.getFavoriteFlights()

    private fun getPossibleFlightsByDepartureAirport(): Flow<List<FlightDetails>> =
        with(_flightSearchUiState.value.departureAirportDetails) {
            InterimAirportDataProvider.getPossibleFlightsBy(iataCode)
        }

    private fun getPossibleFlightsByDepartureAirportzzz(): Flow<List<FlightDetails>> =
        with(_flightSearchUiState.value.departureAirportDetails) {
            flightSearchRepository.getPossibleFlights(iataCode)
                .mapNotNull {
                    it.map {flight ->
                        flight.toFlightDetails()
                    }
                }
        }

    private fun getFlightResultsType(searchValue: String): FlightResultsType =
        if (isFavoriteFlightsDisplayed(searchValue)) {
            Log.i("uistate", "getFlightResultsType() - favorite flights")
            FlightResultsType.FAVORITE_FLIGHTS
        } else if (_flightSearchUiState.value.resultsLabel.isNotBlank()) {
            Log.i("uistate", "getFlightResultsType() - possible flights")
            FlightResultsType.POSSIBLE_FLIGHTS
        } else {
            Log.i("uistate", "getFlightResultsType() - NO flights")
            FlightResultsType.NONE
        }

    private fun isFavoriteFlightsDisplayed(searchValue: String): Boolean =
        InterimAirportDataProvider.hasFavorites() && searchValue.isBlank()

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        val factory: ViewModelProvider.Factory = viewModelFactory {
            Log.i("uistate", "start view model factory - call initializer")
            initializer {
                FlightsViewModel(flightSearchApplication().container.flightSearchRepository)
            }
        }
    }

}

fun CreationExtras.flightSearchApplication(): FlightSearchApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlightSearchApplication)

data class FlightSearchUiState(
    val airportDropdownExpanded: Boolean = false,
    val searchValue: String = "",
    val departureAirportDetails: AirportDetails = AirportDetails(0, "", "", 0),
    val favoritesLabel: String = "Favorite routes",
    val resultsLabel: String = "",
    val noResultsLabel: String = "",
    val favoriteFlightModifications: Int = 0,
    val flightResultsType: FlightResultsType = FlightResultsType.NONE
)

data class AirportResultsUiState(val airportDetailsList: List<AirportDetails> = listOf())

data class FlightResultsUiState(val flightDetailsList: List<FlightDetails> = listOf())

// for add favorite events
fun FlightDetails.toFavorite(): Favorite = Favorite(
    departureCode = departureIataCode,
    destinationCode = arrivalIataCode
)

fun Flight.toFlightDetails(): FlightDetails = FlightDetails(
    departureIataCode = departureIataCode,
    departureAirportName = departureAirportName,
    arrivalIataCode = arrivalIataCode,
    arrivalAirportName = arrivalAirportName,
    isFavorite = isFavorite
)

fun Airport.toAirportDetails(): AirportDetails = AirportDetails(
    id = id,
    iataCode = iataCode,
    name = name,
    passengers = passengers
)
