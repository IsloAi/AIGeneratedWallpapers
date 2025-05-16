package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse

interface FullViewImage {
    fun getFullImageUrl(image: SingleDatabaseResponse)
}