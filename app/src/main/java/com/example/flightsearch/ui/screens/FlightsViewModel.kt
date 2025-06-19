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
import com.example.flightsearch.data.UserPreferencesRepository
import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.domain.FlightDetails
import com.example.flightsearch.domain.FlightResultsType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
import kotlinx.coroutines.withContext

class FlightsViewModel(
    private val flightSearchRepository: FlightSearchRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _countFavoritesFlow: StateFlow<Int> = flightSearchRepository.countFavoritesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = 0
        )

    private val _flightSearchUiState = MutableStateFlow(
        FlightSearchUiState()
    )
    val flightSearchUiState: StateFlow<FlightSearchUiState> = _flightSearchUiState

    private val _resultsLabelUiState = MutableStateFlow("")
    val resultsLabelUiState: StateFlow<String> = _resultsLabelUiState

    // This is a StateFlow - I want the airport list to be read-only.
    var displayAirportDetailsUiState = getAirportDetailsStateFlow()

    // This is a StateFlow - I want the flight list to be read-only.
    // The Room unidirectional-data-flow feature will always push new results
    // when any table in a query has updates.
    // It is the business rules in the view model that are responsible to manage
    // what query points to this StateFlow.
    var displayFlightDetailsUiState: StateFlow<FlightResultsUiState> =
        emptyFlow<FlightResultsUiState>()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FlightResultsUiState(emptyList())
            )

    init {
        Log.i("uistate", "initialized FlightViewModel")
        initializeFlightSearchInformation()
    }

    fun toggleAirportDropdown(expanded: Boolean) {
        Log.i("uistate", "start FlightsViewModel toggleAirportDropdown($expanded)")
        _flightSearchUiState.update {
            it.copy(
                airportDropdownExpanded = expanded
            )
        }
    }

    fun onSearchValueChanged(modifiedSearchValue: String) {

        Log.i("uistate", "start FlightsViewModel onSearchValueChanged($modifiedSearchValue)")

        // set search value state
        _flightSearchUiState.update {
            it.copy(
                searchValue = modifiedSearchValue
            )
        }

        // store search value in user preferences
        viewModelScope.launch {
            userPreferencesRepository.saveAirportPreference(modifiedSearchValue)
        }

        refreshAirportDetails() // dependent on search value state, set above

        // The next two call the database, and change a value that the following code
        // depends on.

        useFlightResultsType(searchValue = modifiedSearchValue) { flightResultsType, countFavorites ->

            _flightSearchUiState.update {
                it.copy(
                    flightResultsType = flightResultsType,
                    favoritesLabel = "Favorite routes ($countFavorites)"
                )
            }

            updateLabelUiState() // dependent on flight results type and favorites label

            // There is a chance that the user has favorites.
            // Then the flight details state flow is made to point to the favorites DAO
            if (
                modifiedSearchValue.isBlank() &&
                _flightSearchUiState.value.flightResultsType == FlightResultsType.FAVORITE_FLIGHTS
            ) {
                chooseFlightDetailsFlow()
            }
        }
    }

    fun checkWhenToSwitchFromFavoriteToPossibleFlightsFlow() {
        Log.i("uistate", "start FlightsViewModel reviewUiStateForNecessaryUpdates()")

        with(_flightSearchUiState) {

            // When the list presently shows possible flights, then skip
            if (value.flightResultsType != FlightResultsType.FAVORITE_FLIGHTS) return;

            // When this point in the code is reached,
            // then the present flight results type is FAVORITE_FLIGHTS

            // Run the business rule flight results type with no search value,
            // which indicates user is viewing favorites.
            // When search value is not passed to useFlightResultsType,
            // then "" is the search value, which is what is desired here.
            useFlightResultsType { flightResultsType, countFavorites ->

                // Based on the flight results type response,

                // 1. pick the correct search value to restore.
                val updatedSearchValue =
                    if (flightResultsType == FlightResultsType.POSSIBLE_FLIGHTS) {
                        // when the favorite flight list is empty after the last item is removed,
                        // then restore the search value of the last selected departure airport.
                        value.departureAirportDetails.iataCode
                    } else {
                        // otherwise, when the favorite flight list is not empty,
                        // then retain a blank search value.
                        ""
                    }

                // 2. update the search value UI state.
                update {
                    it.copy(
                        flightResultsType = flightResultsType,
                        searchValue = updatedSearchValue,
                        favoritesLabel = "Favorite routes ($countFavorites)"
                    )
                }
                updateLabelUiState() // dependent on flight results type set above

                // 3. update flight details UI state when flight results type
                //    transitions from favorite flights to possible flights.
                val reloadFlightDetails = flightResultsType == FlightResultsType.POSSIBLE_FLIGHTS
                if (reloadFlightDetails) {
                    reloadDisplayFlights()
                }
            }
        }
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
        _resultsLabelUiState.value = getLabel()
        reloadDisplayFlights()
    }

    // When this is called, a list item was clicked and the isFavorite flag was toggled.
    suspend fun toggleFavoriteStatusOfSelectedFlight(toggledFlightDetails: FlightDetails) {

        Log.i("uistate", "value of isFavorite is ${toggledFlightDetails.isFavorite}")

        if (!toggledFlightDetails.isFavorite) {
            removeFavorite(toggledFlightDetails)
        }
        if (toggledFlightDetails.isFavorite) {
            addFavorite(toggledFlightDetails)
        }

    }

    private fun refreshAirportDetails() {
        displayAirportDetailsUiState = getAirportDetailsStateFlow()
    }

    private fun initFlightResultsType() {
        with(_flightSearchUiState.value) {
            useFlightResultsType(searchValue = searchValue) { flightResultsType, _ ->
                _flightSearchUiState.update {
                    it.copy(
                        flightResultsType = flightResultsType
                    )
                }
                updateLabelUiState() // dependent on flight results type
                reloadDisplayFlights() // dependent on flight results type
            }
        }
    }

    private fun initializeFlightSearchInformation() {
        viewModelScope.launch {
            // The airport search value is collected from the preferences cold flow
            // since there are no hot flow re-compositions that need to occur.
            userPreferencesRepository.currentAirportSearchValue.collect {storedSearchValue ->
                if (storedSearchValue.isNotBlank()) {
                    _flightSearchUiState.update {
                        it.copy(
                            searchValue = storedSearchValue
                        )
                    }
                    refreshAirportDetails()
                }
                initFlightResultsType() // depends on possible stored search value
            }
        }
    }

    private fun useFlightResultsType(
        searchValue: String = "",
        doWork: (FlightResultsType, Int) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val hasFavorites = async { flightSearchRepository.hasFavorites() }.await()
                val countFavorites = async { flightSearchRepository.countFavorites() }.await()
                val countFavoritesFromFlow = _countFavoritesFlow.value
                Log.i("uistate", "count favorites by async = $countFavorites")
                Log.i("uistate", "count favorites by hot flow = $countFavoritesFromFlow")
                doWork(
                    getNewFlightResultsType(hasFavorites, searchValue),
                    countFavorites
                )
            }
        }
    }

    private fun getNewFlightResultsType(hasFavorites: Boolean, searchValue: String) =
        if (hasFavorites && searchValue.isBlank()) {
            Log.i("uistate", "getFlightResultsType() - favorite flights")
            FlightResultsType.FAVORITE_FLIGHTS
        } else if (_flightSearchUiState.value.resultsLabel.isNotBlank()) {
            Log.i("uistate", "getFlightResultsType() - possible flights")
            FlightResultsType.POSSIBLE_FLIGHTS
        } else {
            Log.i("uistate", "getFlightResultsType() - NO flights")
            FlightResultsType.NONE
        }

    // This method exists for readability in the context of what method calls it.
    // @see onSearchValueChanged(modifiedSearchValue)
    private fun chooseFlightDetailsFlow() {
        reloadDisplayFlights()
    }

    private fun updateLabelUiState() {
        _resultsLabelUiState.value = getLabel()
    }

    private suspend fun addFavorite(toggledFlightDetails: FlightDetails) {
        Log.i(
            "uistate",
            "start add favorite flight (${toggledFlightDetails.departureIataCode}, ${toggledFlightDetails.arrivalIataCode})"
        )
        val favoriteFlight = toggledFlightDetails.toFavorite()
        flightSearchRepository.insert(favoriteFlight)
    }

    private suspend fun removeFavorite(toggledFlightDetails: FlightDetails) {
        Log.i(
            "uistate",
            "start remove favorite flight (${toggledFlightDetails.departureIataCode}, ${toggledFlightDetails.arrivalIataCode})"
        )
        // De-structure the selected flight details
        val (_, departureCode, _, destinationCode, _, _) = toggledFlightDetails
        flightSearchRepository.deleteBy(departureCode, destinationCode)
    }

    private fun getLabel(): String = with(_flightSearchUiState.value) {
        when (flightResultsType) {
            FlightResultsType.FAVORITE_FLIGHTS -> favoritesLabel
            FlightResultsType.POSSIBLE_FLIGHTS -> resultsLabel
            FlightResultsType.NONE -> noResultsLabel
        }
    }

    private fun getAirportDetailsStateFlow(): StateFlow<AirportResultsUiState> =
        getAirportDetail()
            .mapNotNull {
                it.map { airport ->
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

    private fun getAirportDetail(): Flow<List<Airport>> =
        with(_flightSearchUiState.value) {
            if (searchValue.isBlank()) return emptyFlow()
            return flightSearchRepository.getAirportsBy("%$searchValue%")
        }

    private fun getDisplayFlights(): Flow<List<FlightDetails>> = with(_flightSearchUiState.value) {
        when (flightResultsType) {
            FlightResultsType.FAVORITE_FLIGHTS -> getFavoriteFlight()
            FlightResultsType.POSSIBLE_FLIGHTS -> getPossibleFlightsByDepartureAirport()
            FlightResultsType.NONE -> flow { emptyList<FlightDetails>() }
        }
    }

    private fun reloadDisplayFlights() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getDisplayFlights()
                    .map { FlightResultsUiState(it) }
                    .filter {
                        Log.i("uistate", "created FlightResultsUiState of list of flight details")
                        true
                    }
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                        initialValue = FlightResultsUiState()
                    ).apply {
                        displayFlightDetailsUiState = this
                    }
            }
        }
    }

    private fun getFavoriteFlight(): Flow<List<FlightDetails>> =
        flightSearchRepository.getFavoriteFlights()
            .mapNotNull {
                it.map { flight ->
                    flight.toFlightDetails()
                }
            }

    private fun getPossibleFlightsByDepartureAirport(): Flow<List<FlightDetails>> =
        with(_flightSearchUiState.value.departureAirportDetails) {
            flightSearchRepository.getPossibleFlights(iataCode)
                .mapNotNull {
                    it.map { flight ->
                        flight.toFlightDetails()
                    }
                }
        }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        val factory: ViewModelProvider.Factory = viewModelFactory {
            Log.i("uistate", "start view model factory - call initializer")
            initializer {
                FlightsViewModel(
                    flightSearchApplication().container.flightSearchRepository,
                    flightSearchApplication().container.userPreferencesRepository
                )
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

// for mapping/transforming DAO flight items into Domain items of flight details
fun Flight.toFlightDetails(): FlightDetails = FlightDetails(
    departureIataCode = departureIataCode,
    departureAirportName = departureAirportName,
    arrivalIataCode = arrivalIataCode,
    arrivalAirportName = arrivalAirportName,
    isFavorite = isFavorite
)

// for mapping/transforming DAO airport items into Domain items of airport details
fun Airport.toAirportDetails(): AirportDetails = AirportDetails(
    id = id,
    iataCode = iataCode,
    name = name,
    passengers = passengers
)
