package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel

@Dao
interface LiveWallpaperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseResponse: LiveWallpaperModel)

    @Query("SELECT * FROM liveWallpaper")
    fun getAllWallpapers(): List<LiveWallpaperModel>

    @Query("SELECT * FROM liveWallpaper WHERE catname =:cat ")
    fun getLiveCategoryWallpaper(cat: String): List<LiveWallpaperModel>

    @Query("UPDATE liveWallpaper SET liked=:liked WHERE id=:Id")
    fun updateLiveFavourite(liked: Boolean, Id: Int)

    @Query("SELECT * FROM liveWallpaper where liked=1")
    suspend fun getLiveFavourites(): List<LiveWallpaperModel>

    /*@Query("SELECT * FROM liveWallpaper WHERE catname =:cat ")
    fun getCatgoriesWallpapers(cat: String): List<LiveWallpaperModel>

    @Query("UPDATE liveWallpaper SET unlocked=:liked WHERE id=:Id")
    fun updateLocked(liked: Boolean, Id: Int)

    @Query("SELECT * FROM liveWallpaper ORDER BY downloads DESC LIMIT (:limit)")
    suspend fun getTopDownloadedWallpapers(limit: Int): List<LiveWallpaperModel>

    @Update
    suspend fun updateWallpapers(wallpapers: List<LiveWallpaperModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveFavourite(databaseResponse: FavouriteLiveModel)

    @Query("DELETE FROM LiveFavourite WHERE id = :wallpaperId")
    suspend fun deleteLiveFavourite(wallpaperId: String)

    @Query("DELETE FROM LiveFavourite")
    suspend fun deleteAllLiveFavourites()

    @Query("SELECT * FROM LiveFavourite")
    fun getAllFavouriteWallpapers(): List<FavouriteLiveModel>*/

}