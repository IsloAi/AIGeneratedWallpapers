package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [GetResponseIGEntity::class,FavouriteListIGEntity::class ], version = 6)
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

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val cursor = database.query("PRAGMA table_info(get_response_ig)")
                var columnExists = false
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        val columnName = cursor.getString(cursor.getColumnIndex("name"))
                        if (columnName == "isSelected") {
                            columnExists = true
                            break
                        }
                    }
                    cursor.close()
                }

                // If the column doesn't exist, add it
                if (!columnExists) {
                    database.execSQL("ALTER TABLE get_response_ig ADD COLUMN isSelected INTEGER NOT NULL DEFAULT 0")
                }
            }
        }
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "appDatabase"
            ).allowMainThreadQueries()
                .addMigrations(MIGRATION_5_6)
                .build()
        }

    }
}