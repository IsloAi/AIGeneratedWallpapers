package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GetResponseIGDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(getResponseIG: GetResponseIGEntity)

    @Query("SELECT * FROM get_response_ig WHERE id = :id")
    fun getGetResponseIGByID(id: Int): LiveData<GetResponseIGEntity?>


    @Query("SELECT * FROM get_response_ig WHERE id=:Id")
    fun getCreationsByIdNotLive(Id:Int):GetResponseIGEntity

    @Query("SELECT * FROM get_response_ig")
    fun getAllGetResponseIG(): LiveData<List<GetResponseIGEntity>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun UpdateData(genericResponseModel: GetResponseIGEntity)


    @Query("DELETE FROM get_response_ig")
    fun deleteAllCreations()

    @Delete
    fun deleteCreation(genericResponseModel: GetResponseIGEntity):Int

    @Delete
    suspend fun deleteCreations(creations: List<GetResponseIGEntity>)
}
