package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models

import com.google.gson.annotations.SerializedName


data class MostDownloadImageResponse(
    val id: Int,
    val cat_name: String,
    val image_name: String,
    val hd_image_url: String,
    val compressed_image_url: String,
    val img_size: Float,
    val gems: Int,
    val likes: Int,
    val liked: Boolean,
    val unlockimges: Boolean,
    val Tags: String,
    val capacity: String
)