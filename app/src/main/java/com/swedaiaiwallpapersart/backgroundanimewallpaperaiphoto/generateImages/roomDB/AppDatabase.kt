package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [GetResponseIGEntity::class,FavouriteListIGEntity::class ], version = 4)
@TypeConverters(ArrayListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getResponseIGDao(): GetResponseIGDao
    abstract fun getFavouriteList(): FavouriteListIGDao


    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "appDatabase"
            ).allowMainThreadQueries()
             .build()
        }

    }
}