package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models

import com.google.gson.annotations.SerializedName


data class MostDownloadImageResponse (
    @SerializedName("download_count")
    val downloadCount: String?,
    @SerializedName("hd_image_url")
    val hdImageUrl: String?,
    @SerializedName("compressed_image_url")
    val compressedImageUrl: String?
)