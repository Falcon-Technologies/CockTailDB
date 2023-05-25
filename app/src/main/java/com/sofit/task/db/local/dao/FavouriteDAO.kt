package com.sofit.task.db.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sofit.task.db.local.data.FavouriteEntity

// A DAO is a data access object, we use this to manipulate the data for our SQLite Room database
@Dao
interface FavouriteDao {
    // Room implements these methods

    // this insert deals with creating by inserting and also updates by replacing them
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: FavouriteEntity)

    // favourites is defined in FavouriteEntity as the table name
    @Query("SELECT * FROM favourites WHERE id = :id")
    // Use ? as the object may be null - i.e. now entry in the DB for that id.
    suspend fun getFavouriteById(id: Int): FavouriteEntity?


    @Query("DELETE FROM favourites WHERE id = :id")
    suspend fun removeFavourite(id: Int)

    @Query("SELECT * FROM favourites")
    suspend fun getAll(): MutableList<FavouriteEntity?>?

}