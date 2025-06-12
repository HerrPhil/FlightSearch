package com.example.flightsearch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // want to auto-increment
    @ColumnInfo(name = "departure_code")
    val departureCode: String, // db name departure_code
    @ColumnInfo(name = "destination_code")
    val destinationCode: String // db name destination_code
)
