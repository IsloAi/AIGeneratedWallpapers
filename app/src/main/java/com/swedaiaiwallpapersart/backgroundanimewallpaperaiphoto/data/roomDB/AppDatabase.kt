package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao.ChargingAnimationDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao.DoubleWallpaperDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao.LiveWallpaperDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao.WallpapersDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ArrayListStringConverter

@Database(
    entities = [SingleDatabaseResponse::class, LiveWallpaperModel::class, ChargingAnimModel::class, DoubleWallModel::class],
    version = 14,
    exportSchema = false
)
@TypeConverters(ArrayListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wallpapersDao(): WallpapersDao

    abstract fun liveWallpaperDao(): LiveWallpaperDao
    abstract fun chargingAnimDao(): ChargingAnimationDao
    abstract fun doubleWallpaperDao(): DoubleWallpaperDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `liveWallpaper` (" +
                            "`id` TEXT PRIMARY KEY NOT NULL, " +
                            "`livewallpaper_url` TEXT, " +
                            "`thumnail_url` TEXT, " +
                            "`videoSize` REAL, " +
                            "`liked` INTEGER NOT NULL DEFAULT 0, " +
                            "`download` INTEGER NOT NULL DEFAULT 0, " +
                            "`unlocked` INTEGER NOT NULL DEFAULT 1" +
                            ")"
                )

                database.execSQL("ALTER TABLE allWallpapers ADD COLUMN unlocked INTEGER NOT NULL DEFAULT 1")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Perform migration steps for version 7 to version 8
                // Add any necessary changes, such as creating the new table
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `allWallpapers` (" +
                            "`id` INTEGER PRIMARY KEY NOT NULL, " +
                            "`cat_name` TEXT, " +
                            "`image_name` TEXT, " +
                            "`hd_image_url` TEXT, " +
                            "`compressed_image_url` TEXT, " +
                            "`likes` INTEGER, " +
                            "`liked` INTEGER, " +
                            "`size` INTEGER, " +
                            "`Tags` TEXT, " +
                            "`capacity` TEXT" +
                            ")"
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            @SuppressLint("Range")
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
                "4K_Wallpaper_Database"
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }

    }
}