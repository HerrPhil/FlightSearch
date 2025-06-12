package com.example.flightsearch.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextFieldValue.Companion.Saver
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R
import com.example.flightsearch.domain.AirportDetails
import com.example.flightsearch.ui.screens.AirportResultsUiState
import com.example.flightsearch.ui.screens.FlightSearchUiState
import com.example.flightsearch.ui.theme.FlightSearchTheme
import com.example.flightsearch.utils.getFormattedAirport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteSearchTextField(
    flightSearchUiState: FlightSearchUiState,
    airportResultsUiState: AirportResultsUiState,
    toggleAirportDropdown: (Boolean) -> Unit,
    collapseAirportDropdown: () -> Unit,
    onValueChange: (String) -> Unit,
    onSetDepartureSelection: (AirportDetails) -> Unit,
    modifier: Modifier = Modifier
) {

    Log.i("FilteredSearch", "In AutoCompleteSearchTextField")

    val textFieldValue = TextFieldValue(
        text = flightSearchUiState.searchValue,
        selection = TextRange(flightSearchUiState.searchValue.length)
    )

    ExposedDropdownMenuBox(
        expanded = flightSearchUiState.airportDropdownExpanded,
        onExpandedChange = toggleAirportDropdown,
        modifier = modifier
    ) {
        TextField(
            value = textFieldValue,
            onValueChange = { filterValue -> onValueChange(filterValue.text) },
            label = {
                Text(text = stringResource(R.string.enter_departure_airport))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = flightSearchUiState.airportDropdownExpanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors().copy(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(percent = 10),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .menuAnchor(type = MenuAnchorType.SecondaryEditable, enabled = true)
        )
        val options = airportResultsUiState.airportDetailsList
        ExposedDropdownMenu(
            expanded = flightSearchUiState.airportDropdownExpanded,
            onDismissRequest = collapseAirportDropdown
        ) {
            options.forEach { selectionOption ->

                val annotatedAirportDetails =
                    getFormattedAirport(selectionOption.iataCode, selectionOption.name)

                DropdownMenuItem(
                    text = {
                        Text(text = annotatedAirportDetails)
                    },
                    onClick = {
                        Log.i("FilteredSearch", "MenuItem onClick")
                        collapseAirportDropdown()
                        onSetDepartureSelection(selectionOption)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }

        }

    }

}

@Preview(showBackground = true)
@Composable
fun AutoCompleteSearchTextFieldPreview() {
    FlightSearchTheme {
        AutoCompleteSearchTextField(
            FlightSearchUiState(),
            AirportResultsUiState(),
            toggleAirportDropdown = {},
            collapseAirportDropdown = {},
            onValueChange = {},
            onSetDepartureSelection = {}
        )
    }
}
