package com.example.flightsearch.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.flightsearch.R
import com.example.flightsearch.ui.theme.FlightSearchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {

    Log.i("FilteredSearch", "In AutoCompleteSearchTextField")

    // TODO implement the search results text field drop down filtering
//    Box {
//        Text(text = "TODO - add the rest of the auto-complete search text field, with dropdown, here")
//    }

    // TODO compose the complex widget using ExposedDropdownMenuBox, TextField,
    //      ExposedDropdownMenu, and DropDownMenuItem

    var expanded by remember { mutableStateOf(false) }
    var filteredOptions by remember(value) {
        mutableStateOf(options.filter { option ->
            option.contains(
                value,
                ignoreCase = true
            )
        })
    }
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        )
    }

    Log.i("FilteredSearch", "number of options: ${options.size}")
    Log.i("FilteredSearch", "number of filtered options: ${filteredOptions.size}")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = textFieldValueState,
            onValueChange = { newValue ->
                Log.i("FilteredSearch", "TextField onValueChange")
                textFieldValueState = newValue.copy(selection = TextRange(newValue.text.length))
                onValueChange(newValue.text)
                filteredOptions = options.filter {
                    it.contains(
                        newValue.text,
                        ignoreCase = true
                    )
                }
            },
            label = {
                Text(text = stringResource(R.string.enter_departure_airport))
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded && textFieldValueState.text.isNotBlank()
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.SecondaryEditable, enabled = true)
                .clickable {

                }
        )
        ExposedDropdownMenu(
            expanded = expanded && textFieldValueState.text.isNotBlank(),
            onDismissRequest = { expanded = false }
        ) {
            filteredOptions.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(text = selectionOption)
                    },
                    onClick = {
                        Log.i("FilteredSearch", "MenuItem onClick")
                        onValueChange(selectionOption)
                        textFieldValueState = TextFieldValue(
                            text = selectionOption,
                            selection = TextRange(selectionOption.length)
                        )
                        expanded = false
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
        AutoCompleteSearchTextField("test", { }, listOf("abc", "def", "ghi"))
    }
}
