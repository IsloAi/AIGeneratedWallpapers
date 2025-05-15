package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel

@Dao
interface DoubleWallpaperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseResponse: DoubleWallModel)

    @Query("SELECT * FROM Double_Wallpaper")
    suspend fun getAllDoubleWallpapers(): List<DoubleWallModel>

}