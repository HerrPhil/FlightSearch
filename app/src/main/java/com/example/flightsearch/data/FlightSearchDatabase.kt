package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
abstract class FlightSearchDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun flightDao(): FlightDao
    companion object {
        @Volatile
        private var Instance: FlightSearchDatabase? = null
        fun getDatabase(context: Context): FlightSearchDatabase {
            // If the Instance of the database is not null, return it.
            // Otherwise, create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    FlightSearchDatabase::class.java,
                    "flight_search_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .createFromAsset("database/flight_search.db")
                    .build()
                    .also {
                        Instance = it
                    }

            }
        }
    }
}