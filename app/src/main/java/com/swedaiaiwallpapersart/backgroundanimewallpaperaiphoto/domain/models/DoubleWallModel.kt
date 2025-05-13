package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Double_Wallpaper")
data class DoubleWallModel(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val hd_url1: String,
    val compress_url1: String,
    val size1: Int,
    val hd_url2: String,
    val compress_url2: String,
    val size2: Int,
    var downloaded: Boolean = false
)
