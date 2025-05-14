package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository

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

}