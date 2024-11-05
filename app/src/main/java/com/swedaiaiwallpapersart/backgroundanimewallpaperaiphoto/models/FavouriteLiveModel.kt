package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LiveFavourite")
data class FavouriteLiveModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "wallpaper_id")
    val wallpaperId: String,
    @ColumnInfo(name = "liveWallpaper_url")
    val livewallpaper_url: String,
    @ColumnInfo(name = "thumbnail_url")
    val thumnail_url: String,
    @ColumnInfo(name = "videoSize")
    val videoSize: Float,
    @ColumnInfo(name = "Liked")
    var liked: Boolean,
    @ColumnInfo(name = "downloads")
    val downloads: Int,
    @ColumnInfo(name = "catname")
    var catname: String? = null,
    @ColumnInfo(name = "Likes")
    var likes: Int,
    @ColumnInfo(name = "unlocked")
    var unlocked: Boolean = true
){
    constructor(
        wallpaperId: String,
        livewallpaper_url: String,
        thumnail_url: String,
        videoSize: Float,
        liked: Boolean,
        downloads: Int,
        catname: String? = null,
        likes: Int,
        unlocked: Boolean = true
    ) : this(0, wallpaperId, livewallpaper_url, thumnail_url, videoSize, liked, downloads, catname, likes, unlocked)
}
