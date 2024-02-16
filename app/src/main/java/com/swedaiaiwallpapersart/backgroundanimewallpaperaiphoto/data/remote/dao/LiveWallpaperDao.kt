package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel

@Dao
interface LiveWallpaperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseResponse: LiveWallpaperModel)

    @Query("SELECT * FROM liveWallpapers")
    fun getAllWallpapers():List<LiveWallpaperModel>


    @Query("UPDATE liveWallpapers SET unlocked=:liked WHERE id=:Id")
    fun updateLocked(liked:Boolean,Id: Int)
}