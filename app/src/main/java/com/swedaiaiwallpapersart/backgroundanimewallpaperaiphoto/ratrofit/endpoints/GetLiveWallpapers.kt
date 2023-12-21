package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveImagesResponse
import retrofit2.Call
import retrofit2.http.GET


interface GetLiveWallpapers {

    @GET("getLiveWallpaper.php")
    fun getLiveWallpapers(): Call<LiveImagesResponse>
}