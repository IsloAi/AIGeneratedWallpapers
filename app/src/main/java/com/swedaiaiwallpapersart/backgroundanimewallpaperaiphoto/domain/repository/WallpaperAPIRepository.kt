package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.ChargingAnimationResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.DoubleApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.LiveApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.StaticApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CategoryApiModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WallpaperAPIRepository {

    //FETCHING ALL STATIC WALLPAPERS
    suspend fun getAllWallpapers(): Flow<Response<StaticApiResponse>>

    //FETCHING ALL LIVE WALLPAPERS
    suspend fun getLiveWallpaper(): Flow<Response<LiveApiResponse>>

    //FETCHING CHARGING ANIMATIONS
    fun getChargingAnimation(): Flow<Response<ChargingAnimationResponse>>

    //FETCHING DOUBLE WALLPAPERS
    fun getDoubleWallpapers(): Flow<Response<DoubleApiResponse>>

    //FETCHING CATEGORIES
    fun getCategories(): Flow<Response<List<CategoryApiModel>>>


}