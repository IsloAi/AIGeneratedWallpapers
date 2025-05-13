package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Charging_Animation")
data class ChargingAnimModel(
    val extension: String,
    @PrimaryKey(autoGenerate = false)
    val thumnail: String,
    val hd_animation: String
)
