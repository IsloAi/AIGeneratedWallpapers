package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeListInterface {
    @GET("getalllikes.php")
    fun getList(): Call<FavouriteListResponse>
}