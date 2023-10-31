package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces

interface GetFavouriteImagePath {
    fun getPath(path:String)
    fun getImageClick(position: Int, prompt: String?, imageId: Int)
}