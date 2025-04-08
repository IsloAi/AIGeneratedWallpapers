package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import retrofit2.Call
import retrofit2.http.GET

interface AllWallpapers {
    @GET("all.php?record=4000")
    fun getList(): Call<FavouriteListResponse>
}