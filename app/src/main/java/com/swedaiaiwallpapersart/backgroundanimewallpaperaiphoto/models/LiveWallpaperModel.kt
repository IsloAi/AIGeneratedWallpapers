package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liveWallpaper")
data class LiveWallpaperModel(
    @PrimaryKey(autoGenerate = false)
    val id:String,
    val livewallpaper_url:String,
    val thumnail_url:String,
    val videoSize:Float,
    var liked:Boolean,
    val downloads:Int,
    var catname:String ?=  null,
    var likes:Int,
    var unlocked:Boolean = true
)
