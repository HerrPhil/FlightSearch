package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Notes from the course:
// The database operations can take a long time to execute, so they need to run on a separate thread.
// Room does not allow database access on the main thread.

@Dao
interface AirportDao {

    // Notes from the course:
    // For @Query calls that get data, it is recommended to use "Flow" in the persistence layer.
    // With "Flow" as the return type, you receive notification whenever the data in the database
    // changes. The "Room" keeps this "Flow" updated for you, which means

    // The view model domain logic must pre-pend and post-pend '%' to the search value string.
    @Query("SELECT * from airport WHERE iata_code LIKE :searchValue OR name LIKE :searchValue ORDER BY passengers DESC")
    fun getAirportsBy(searchValue: String): Flow<List<Airport>>
}
