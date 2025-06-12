package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Notes from the course:
// The database operations can take a long time to execute, so they need to run on a separate thread.
// Room does not allow database access on the main thread.

@Dao
interface FavoriteDao {

    @Query("SELECT COUNT(*) FROM favorite")
    fun countFavorites(): Int

    @Query(
        "SELECT " +
                "CASE " +
                "WHEN COUNT(*) > 0 THEN 1 " +
                "WHEN COUNT(*) = 0 THEN 0 " +
                "ELSE 0 " +
                "END " +
                "AS has_favorites " +
                "FROM favorite"
    )
    fun hasFavorites(): Boolean

    @Query(
        "SELECT " +
                "EXISTS " +
                "(SELECT 1 " +
                "FROM favorite " +
                "WHERE departure_code = :departureCode " +
                "AND destination_code = :destinationCode) " +
                "AS has_favorite"
    )
    fun hasFavorite(departureCode: String, destinationCode: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteBy(departureCode: String, destinationCode: String)
}
