package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CatResponse

interface FullViewImage {
    fun getFullImageUrl(image: CatResponse)
}