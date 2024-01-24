package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.TokenResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import retrofit2.http.GET
import retrofit2.http.Header

import retrofit2.http.Query

interface EndPointsInterface {

    @GET("generateKey.php")
    suspend fun generateDeviceToken(
        @Query("deviceid") deviceId: String
    ): retrofit2.Response<Unit>

    @GET("all.php")
    suspend fun getAllWallpapers(
        @Header("Authorization") apiKey:String,
        @Query("page") page:String,
        @Query("record") record:String

    ):retrofit2.Response<ListResponse>
}