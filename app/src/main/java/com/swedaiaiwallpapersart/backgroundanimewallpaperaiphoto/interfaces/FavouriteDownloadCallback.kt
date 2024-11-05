package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteLiveModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel

//Created on 5-11-24 by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

interface FavouriteDownloadCallback {
    fun getPosition(position:Int,model: FavouriteLiveModel)
}