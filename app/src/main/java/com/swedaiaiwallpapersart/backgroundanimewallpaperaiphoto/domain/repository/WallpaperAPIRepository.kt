package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository

import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.DoubleWallModel
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.LiveWallpaperModel
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.SingleResponse
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.staticApiResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WallpaperAPIRepository {

    //FETCHING ALL STATIC WALLPAPERS
    suspend fun getAllWallpapers(): Flow<Response<staticApiResponse>>

    //FETCHING ALL LIVE WALLPAPERS
    suspend fun getLiveWallpaper(): Flow<Response<ArrayList<LiveWallpaperModel>>>

    //FETCHING CHARGING ANIMATIONS
    fun getChargingAnimation(): Flow<Response<ArrayList<ChargingAnimModel>>>

    //FETCHING DOUBLE WALLPAPERS
    fun getDoubleWallpapers(): Flow<Response<ArrayList<DoubleWallModel>>>


}