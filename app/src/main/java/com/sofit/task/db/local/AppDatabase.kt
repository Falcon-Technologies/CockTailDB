package com.sofit.task.db.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sofit.task.db.local.dao.FavouriteDao
import com.sofit.task.db.local.data.FavouriteEntity


@Database(entities = [FavouriteEntity::class], version = 1, exportSchema = false)

abstract class AppDatabase: RoomDatabase() {

    abstract fun favouriteDao(): FavouriteDao?

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "favourites.db"
                    )
                        .build()
                }
            }
            return INSTANCE
        }
    }
}