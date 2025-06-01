package com.example.flightsearch.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R
import com.example.flightsearch.data.InterimAirportDataProvider
import com.example.flightsearch.domain.FlightDetails
import com.example.flightsearch.ui.theme.FlightSearchTheme
import com.example.flightsearch.utils.getFormattedAirport

// TODO re-factor flights to be the FlightDetail domain object
@Composable
fun FlightResults(
    resultsLabel: String,
    flights: List<FlightDetails>,
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
            flights = flights,
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
    flights: List<FlightDetails>,
    onClick: (FlightDetails) -> Unit, // This will be the item click to add/remove flight to/from favorites
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp) // inter-composable padding
) {
    // Learned through experimentation that the padding values calculated by Scaffold, at times,
    // can be too much for LazyColumn. For example, the LazyColumn had too much vertical space
    // between its top the the Text composable above it. In this case, simply customize the
    // content padding with PaddingValues that make sense for this UX design.
    LazyColumn(
        contentPadding = contentPadding,
//        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
    ) {
        items(flights, key = { flight -> flight.flightID }) { flight ->

            FlightListItem(
                flight = flight,
                onItemClick = onClick
            )

        }
    }
}

@Composable
private fun FlightListItem(
    flight: FlightDetails,
    onItemClick: (FlightDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
        onClick = { onItemClick(flight) }
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {


            Column(
                modifier = Modifier
//                    .fillMaxWidth()
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
                    getFormattedAirport(flight.departureIataCode, flight.departureAirportName),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.arrival_label),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    getFormattedAirport(flight.arrivalIataCode, flight.arrivalAirportName),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = if (flight.favorite) {
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
}

@Preview(showBackground = true)
@Composable
fun FlightResultsPreview() {
    FlightSearchTheme {
        // The preview content padding values are the
        // guesstimate of what Scaffold will calculate in FlightSearchApp
        FlightResults(
            resultsLabel = "Flights from YYC",
            flights = InterimAirportDataProvider.flights,
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
            flights = InterimAirportDataProvider.flights,
            onClick = {},
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlightListItemPreview() {
    FlightSearchTheme {
        FlightListItem(flight = InterimAirportDataProvider.flights[0], {}, modifier = Modifier)
    }
}