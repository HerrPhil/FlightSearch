package com.example.flightsearch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("airport")
data class Airport(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // want to auto-increment
    @ColumnInfo(name = "iata_code")
    val iataCode: String,  // db name iata_code
    val name: String,
    val passengers: Int
)
