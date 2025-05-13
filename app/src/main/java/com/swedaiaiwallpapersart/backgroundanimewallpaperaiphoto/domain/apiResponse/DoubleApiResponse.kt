package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse

import com.google.gson.annotations.SerializedName
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel

data class DoubleApiResponse(
    @SerializedName("images")
    val doubleWallpaper: ArrayList<DoubleWallModel>
)
