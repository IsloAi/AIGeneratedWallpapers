package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse

interface GetWallpaperFromRoomRepository {

    //FETCHING ALL WALLPAPERS
    suspend fun getAllWallpapersFromRoom(): List<SingleDatabaseResponse>

    //FETCHING Trending WALLPAPERS
    suspend fun getTrendingWallpapersFromRoom(): List<SingleDatabaseResponse>

    //FETCHING Popular WALLPAPERS
    suspend fun getPopularWallpapersFromRoom(): List<SingleDatabaseResponse>

    //FETCHING LIVE WALLPAPERS
    suspend fun getAllLiveWallpapersFromRoom(): List<LiveWallpaperModel>

    //FETCHING Double WALLPAPERS
    suspend fun getAllDoubleWallpapersFromRoom(): List<DoubleWallModel>

    //FETCHING CATEGORY WALLPAPERS
    suspend fun getCategoryWallpaperFromRoom(cat: String): List<SingleDatabaseResponse>

    //FETCHING CAR WALLPAPERS
    suspend fun getCarWallpapersFromRoom(): List<SingleDatabaseResponse>

    //FETCHING BATTERY WALLPAPERS
    suspend fun getBatteryWallpapersFromRoom(): List<ChargingAnimModel>

    //FETCHING LIVE CATEGORY WALLPAPERS
    suspend fun getLiveCategoryWallpaperFromRoom(cat: String): List<LiveWallpaperModel>

    //UPDATING FAVOURITE Live WALLPAPERS
    suspend fun updateLiveFavourite(liked: Boolean, Id: Int)

    //FETCHING FAVOURITE Live WALLPAPERS
    suspend fun getLiveFavourites(): List<LiveWallpaperModel>

    //UPDATING FAVOURITE STATIC WALLPAPERS
    suspend fun updateStaticFavourite(liked: Boolean, Id: Int)

    //FETCHING FAVOURITE STATIC WALLPAPERS
    suspend fun getStaticFavourites(): List<SingleDatabaseResponse>

}