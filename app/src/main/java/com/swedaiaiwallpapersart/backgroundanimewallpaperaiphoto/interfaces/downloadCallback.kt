package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel


interface downloadCallback {
    fun getPosition(position:Int,model: LiveWallpaperModel)
}