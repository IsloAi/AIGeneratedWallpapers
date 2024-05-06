package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel


interface DownloadCallbackDouble {
    fun getPosition(position:Int,model: DoubleWallModel)
}