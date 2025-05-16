package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel

@Dao
interface ChargingAnimationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(response: ChargingAnimModel)

    @Query("Select * from Charging_Animation")
    suspend fun getAllAnimations(): List<ChargingAnimModel>
}