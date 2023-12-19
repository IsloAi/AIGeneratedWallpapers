package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.temp


data class VideoItem(
    val id: Int,
    val pageURL: String,
    val type: String,
    val tags: String,
    val duration: Int,
    val pictureId: String,
    val videos: VideoUrls,
    val views: Int,
    val downloads: Int,
    val likes: Int,
    val comments: Int,
    val userId: Int,
    val user: String,
    val userImageURL: String
)