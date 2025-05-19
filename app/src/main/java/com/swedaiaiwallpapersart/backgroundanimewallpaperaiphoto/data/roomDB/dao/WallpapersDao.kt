package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse

@Dao
interface WallpapersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseResponse: SingleDatabaseResponse)

    @Query("SELECT * FROM `Static Wallpapers`")
    fun getAllWallpapers(): List<SingleDatabaseResponse>

    @Query("SELECT * FROM `Static Wallpapers` WHERE likes >= 150 ORDER BY likes DESC limit 500")
    fun getTrendingWallpapers(): List<SingleDatabaseResponse>

    @Query("SELECT * FROM `Static Wallpapers` limit 500")
    fun getPopularWallpaper(): List<SingleDatabaseResponse>

    @Query("SELECT * FROM `Static Wallpapers` WHERE cat_name=:cat")
    fun getCategoryWallpaper(cat: String): List<SingleDatabaseResponse>

    @Query("SELECT * FROM `Static Wallpapers` WHERE cat_name=\"Car\" ")
    suspend fun getCarWallpapers(): List<SingleDatabaseResponse>

    @Query("UPDATE `Static Wallpapers` SET liked=:liked WHERE id=:Id")
    fun updateStaticFavourite(liked: Boolean, Id: Int)

    @Query("SELECT * FROM `Static Wallpapers` where liked=1")
    suspend fun getStaticFavourites(): List<SingleDatabaseResponse>


    /*@Query("SELECT * FROM allWallpapers")
    fun getAllWallpapersLive(): LiveData<List<SingleDatabaseResponse>>

    @Query("SELECT * FROM allWallpapers WHERE cat_name=:cat")
    fun getCategoryWallpaper(cat: String): List<SingleDatabaseResponse>

    @Query("UPDATE allWallpapers SET likes=:totalLikes WHERE id=:Id")
    fun updateLikes(totalLikes: String, Id: Int)

    @Query("UPDATE allWallpapers SET liked=:liked WHERE id=:Id")
    fun updateLiked(liked: Boolean, Id: Int)

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH) // Suppress warning
    @Query("SELECT * FROM allWallpapers WHERE id = :wallpaperId")
    suspend fun getFavouritesByWallpaperId(wallpaperId: String): CatResponse

    @Update
    suspend fun update(singleDatabaseResponse: SingleDatabaseResponse)

    @Query("DELETE FROM allWallpapers WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE allWallpapers SET unlocked=:liked WHERE id=:Id")
    fun updateLocked(liked: Boolean, Id: Int)*/

}