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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R
import com.example.flightsearch.ui.theme.FlightSearchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onGetFlights: () -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {

    Log.i("FilteredSearch", "In AutoCompleteSearchTextField")

    var expanded by remember { mutableStateOf(false) }

    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        )
    }

    Log.i("FilteredSearch", "number of options: ${options.size}")
    Log.i("FilteredSearch", "test for trailing icon and drop down menu")
    Log.i("FilteredSearch", "exposed drop down menu expanded: $expanded")
    Log.i(
        "FilteredSearch",
        "exposed drop down menu expanded text field value is not blank: ${value.isNotBlank()}"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = textFieldValueState,
            onValueChange = { filterValue ->
                Log.i("FilteredSearch", "TextField onValueChange")
                textFieldValueState =
                    filterValue.copy(selection = TextRange(filterValue.text.length))
                onValueChange(filterValue.text)
            },
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
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors().copy(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(percent = 10),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .menuAnchor(type = MenuAnchorType.SecondaryEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(text = selectionOption)
                    },
                    onClick = {
                        Log.i("FilteredSearch", "MenuItem onClick")
                        onValueChange(selectionOption)
                        expanded = false
                        textFieldValueState = TextFieldValue(
                            text = selectionOption,
                            selection = TextRange(selectionOption.length)
                        )

                        // There is no need to pass the selection option.
                        // The view model will have this value.
                        // The onGetFlights lambda expression delegates to a view model function.
                        onGetFlights()
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
        AutoCompleteSearchTextField("test", { }, {}, listOf("abc", "def", "ghi"))
    }
}
