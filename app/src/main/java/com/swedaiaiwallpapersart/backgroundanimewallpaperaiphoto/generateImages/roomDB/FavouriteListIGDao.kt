package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavouriteListIGDao {
    @Insert
    suspend fun insert(favouriteListIGEntity: FavouriteListIGEntity)

    @Query("SELECT * FROM favourite_list")
    fun getAllFavouriteList(): LiveData<List<FavouriteListIGEntity>>

    @Query("DELETE FROM favourite_list WHERE image = :path")
    suspend fun deleteByPath(path: String)

}
