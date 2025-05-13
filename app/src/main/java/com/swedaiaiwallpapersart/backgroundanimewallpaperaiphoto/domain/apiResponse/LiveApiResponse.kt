package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse

import com.google.gson.annotations.SerializedName
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel

data class LiveApiResponse(
    @SerializedName("images")
    val liveWallpaper: ArrayList<LiveWallpaperModel>
)
