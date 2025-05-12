package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.endpoints

import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.ChargingAnimModel
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.DoubleWallModel
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.LiveWallpaperModel
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.SingleResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.staticApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface APIEndpoints {

    //FOR FETCHING ALL THE STATIC WALLPAPERS
    @GET("all.php?record=4000")
    suspend fun getAllStaticWallpapers(): Response<staticApiResponse>

    //FOR FETCHING ALL THE LIVE WALLPAPERS
    @GET("getLiveWallpaper.php")
    suspend fun getLiveWallpapers(): Response<LiveWallpaperModel>

    //FOR FETCHING ALL THE CHARGING ANIMATIONS
    @GET("getanimation.php")
    suspend fun getChargingAnimations(): Response<ChargingAnimModel>

    //FOR FETCHING ALL THE DOUBLE WALLPAPERS
    @GET("getdoublewallpaper.php")
    suspend fun getDoubleWallpapers(): Response<DoubleWallModel>


}