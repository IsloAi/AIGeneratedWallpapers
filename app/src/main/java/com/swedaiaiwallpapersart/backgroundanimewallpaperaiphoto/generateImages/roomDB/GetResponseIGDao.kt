package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GetResponseIGDao {
    @Insert
    suspend fun insert(getResponseIG: GetResponseIGEntity)

    @Query("SELECT * FROM get_response_ig WHERE id = :id")
    fun getGetResponseIGByID(id: Int): LiveData<GetResponseIGEntity?>

    @Query("SELECT * FROM get_response_ig")
    fun getAllGetResponseIG(): LiveData<List<GetResponseIGEntity>>


    @Query("DELETE FROM get_response_ig")
    fun deleteAllCreations()

    @Delete
    fun deleteCreation(genericResponseModel: GetResponseIGEntity):Int
}
