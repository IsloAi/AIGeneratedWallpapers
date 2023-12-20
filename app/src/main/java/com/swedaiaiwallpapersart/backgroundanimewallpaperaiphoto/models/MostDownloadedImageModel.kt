package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models

import com.google.gson.annotations.SerializedName


data class MostDownloadedImageModel (
    @SerializedName("images")
    val images: List<MostDownloadImageResponse>
)