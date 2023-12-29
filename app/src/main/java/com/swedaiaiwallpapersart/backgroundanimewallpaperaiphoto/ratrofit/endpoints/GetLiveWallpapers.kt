package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveImagesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface GetLiveWallpapers {

    @GET("getLiked_Wallpaper.php")
    fun getLiveWallpapers(@Query("deviceid") deviceid:String): Call<LiveImagesResponse>
}