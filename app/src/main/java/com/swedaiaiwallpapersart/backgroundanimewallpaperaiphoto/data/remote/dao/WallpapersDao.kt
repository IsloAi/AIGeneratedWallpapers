package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.GetResponseIGEntity

@Dao
interface WallpapersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseResponse: SingleDatabaseResponse)

    @Query("SELECT * FROM allWallpapers")
    fun getAllWallpapers():List<SingleDatabaseResponse>


    @Query("SELECT * FROM allWallpapers WHERE likes != 0 ORDER BY likes DESC ")
    fun getTrendingWallpapers():List<SingleDatabaseResponse>

    @Query("SELECT * FROM allWallpapers WHERE cat_name=:cat")
    fun getCategoryWallpaper(cat:String):List<SingleDatabaseResponse>

    @Query("UPDATE allWallpapers SET likes=:totalLikes WHERE id=:Id")
    fun updateLikes(totalLikes:String,Id: Int)

    @Query("UPDATE allWallpapers SET liked=:liked WHERE id=:Id")
    fun updateLiked(liked:Boolean,Id: Int)


    @Query("UPDATE allWallpapers SET unlocked=:liked WHERE id=:Id")
    fun updateLocked(liked:Boolean,Id: Int)




}