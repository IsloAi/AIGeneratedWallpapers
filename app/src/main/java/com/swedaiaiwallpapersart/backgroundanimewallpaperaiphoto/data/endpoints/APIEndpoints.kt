package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.endpoints

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.ChargingAnimationResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.DoubleApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.LiveApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.StaticApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CategoryApiModel
import retrofit2.Response
import retrofit2.http.GET

interface APIEndpoints {

    //FOR FETCHING ALL THE STATIC WALLPAPERS
    @GET("all.php?record=4000")
    suspend fun getAllStaticWallpapers(): Response<StaticApiResponse>

    //FOR FETCHING ALL THE LIVE WALLPAPERS
    @GET("getLiveWallpaper.php")
    suspend fun getLiveWallpapers(): Response<LiveApiResponse>

    //FOR FETCHING ALL THE CHARGING ANIMATIONS
    @GET("getanimation.php")
    suspend fun getChargingAnimations(): Response<ChargingAnimationResponse>

    //FOR FETCHING ALL THE DOUBLE WALLPAPERS
    @GET("getdoublewallpaper.php")
    suspend fun getDoubleWallpapers(): Response<DoubleApiResponse>

    //FOR FETCHING ALL THE STATIC CATEGORIES
    @GET("getcategory.php")
    suspend fun getCategories(): Response<List<CategoryApiModel>>


}