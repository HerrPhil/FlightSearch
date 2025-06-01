package com.example.flightsearch.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun getFormattedAirport(iataCode:String, name:String):AnnotatedString {

    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(iataCode)
        }
        append("  ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
            append(name)
        }
    }

}
