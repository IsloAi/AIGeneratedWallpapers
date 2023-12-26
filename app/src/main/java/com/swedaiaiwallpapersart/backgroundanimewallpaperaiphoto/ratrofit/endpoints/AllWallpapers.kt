package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AllWallpapers {
    @GET("all.php")
    fun getList(@Query("uid") uid:String): Call<FavouriteListResponse>
}