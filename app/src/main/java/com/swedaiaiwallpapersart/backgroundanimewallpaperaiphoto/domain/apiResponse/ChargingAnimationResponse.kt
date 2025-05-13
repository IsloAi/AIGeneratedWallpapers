package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse

import com.google.gson.annotations.SerializedName
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel

data class ChargingAnimationResponse(
    @SerializedName("images")
    val chargingAnimations: ArrayList<ChargingAnimModel>
)
