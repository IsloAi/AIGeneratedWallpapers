package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel

@Dao
interface LiveWallpaperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseResponse: LiveWallpaperModel)

    @Query("SELECT * FROM liveWallpaper")
    fun getAllWallpapers():List<LiveWallpaperModel>

    @Query("SELECT * FROM liveWallpaper WHERE catname =:cat ")
    fun getCatgoriesWallpapers(cat:String):List<LiveWallpaperModel>

    @Query("UPDATE liveWallpaper SET unlocked=:liked WHERE id=:Id")
    fun updateLocked(liked:Boolean,Id: Int)

    @Query("SELECT * FROM liveWallpaper ORDER BY downloads DESC LIMIT (:limit)")
    suspend fun getTopDownloadedWallpapers(limit: Int): List<LiveWallpaperModel>

    @Update
    suspend fun updateWallpapers(wallpapers: List<LiveWallpaperModel>)
}