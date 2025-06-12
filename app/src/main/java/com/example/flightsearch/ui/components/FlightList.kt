package com.example.flightsearch.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R
import com.example.flightsearch.data.PreviewAirportDataProvider
import com.example.flightsearch.domain.FlightDetails
import com.example.flightsearch.ui.screens.FlightResultsUiState
import com.example.flightsearch.ui.theme.FlightSearchTheme
import com.example.flightsearch.utils.getFormattedAirport
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

// TODO re-factor flights to be the FlightDetail domain object
@Composable
fun FlightResults(
    resultsLabel: String,
    flightResultsUiState: FlightResultsUiState,
    onClick: (FlightDetails) -> Unit, // This will be the item click to add/remove flight to/from favorites
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    Column(
        modifier = Modifier
    ) {
        // TODO replace with airport code from domain object airport details
        Text(
            text = resultsLabel,
            modifier = modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        )
        FlightList(
            flightResultsUiState,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
//            contentPadding = contentPadding
        )
    }
}

// TODO re-factor flights to be the FlightDetail domain object
@Composable
private fun FlightList(
    flightResultsUiState: FlightResultsUiState,
    onClick: (FlightDetails) -> Unit, // This will be the item click to add/remove flight to/from favorites
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp) // inter-composable padding
) {
    // Learned through experimentation that the padding values calculated by Scaffold, at times,
    // can be too much for LazyColumn. For example, the LazyColumn had too much vertical space
    // between its top the the Text composable above it. In this case, simply customize the
    // content padding with PaddingValues that make sense for this UX design.
    val flightDetailsList = flightResultsUiState.flightDetailsList
    LazyColumn(
        contentPadding = contentPadding,
//        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
    ) {
        items(flightDetailsList, key = { flightDetails -> flightDetails.flightID }) { flightDetails ->
            FlightListItem(
                flightDetails = flightDetails,
                onItemClick = onClick
            )
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun FlightListItem(
    flightDetails: FlightDetails,
    onItemClick: (FlightDetails) -> Unit,
    modifier: Modifier = Modifier
) {

    var isClickedInUI by remember { mutableStateOf(flightDetails.favorite) }

    // The next two variables and the LaunchedEffect further down
    // prevent the user from multiple clicks to stop duplicate entries in the favorites list.
    val debounceClickState = remember {
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1
        )
    }
    val debounceCoroutineScope = rememberCoroutineScope()

    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = modifier.clickable(
            onClick = {
                debounceCoroutineScope.launch {
                    debounceClickState.emit(value = Unit)
                }
            }
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .padding( // intra-item padding
                        vertical = dimensionResource(R.dimen.padding_small),
                        horizontal = dimensionResource(R.dimen.padding_medium)
                    )
            ) {
                Text(
                    text = stringResource(R.string.departure_label),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    getFormattedAirport(flightDetails.departureIataCode, flightDetails.departureAirportName),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.arrival_label),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    getFormattedAirport(flightDetails.arrivalIataCode, flightDetails.arrivalAirportName),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = if (isClickedInUI) {
                    painterResource(R.drawable.favorite_flight)
                } else {
                    painterResource(R.drawable.default_flight)
                },
                contentDescription = stringResource(R.string.favorite_flight_icon),
                tint = Color.Unspecified, // required to show color in drawable resource
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }

    LaunchedEffect(Unit) {
        debounceClickState
            .debounce(500)
            .collect {
                isClickedInUI = !isClickedInUI
                onItemClick(flightDetails)
            }
    }
}

@Preview(showBackground = true)
@Composable
fun FlightResultsPreview() {
    FlightSearchTheme {
        // The preview content padding values are the
        // guesstimate of what Scaffold will calculate in FlightSearchApp
        FlightResults(
            resultsLabel = "Flights from YYC",
            FlightResultsUiState(),
            onClick = {},
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlightListPreview() {
    FlightSearchTheme {
        // The preview content padding values are the
        // guesstimate of what Scaffold will calculate in FlightSearchApp
        FlightList(
            FlightResultsUiState(),
            onClick = {},
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlightListItemPreview() {
    FlightSearchTheme {
        FlightListItem(flightDetails = PreviewAirportDataProvider.flightDetails[0], {}, modifier = Modifier)
    }
}
