package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel

interface downloadCallback {
    fun getPosition(position: Int, model: LiveWallpaperModel)
}