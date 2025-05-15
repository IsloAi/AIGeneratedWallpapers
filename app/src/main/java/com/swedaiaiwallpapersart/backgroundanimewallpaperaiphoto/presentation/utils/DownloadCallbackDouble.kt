package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel


interface DownloadCallbackDouble {
    fun getPosition(position: Int, model: DoubleWallModel)
}