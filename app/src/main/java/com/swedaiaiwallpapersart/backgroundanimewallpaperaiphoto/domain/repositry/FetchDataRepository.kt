package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow


interface FetchDataRepository {

    fun fetechAllWallpapers(): Flow<Response<List<SingleDatabaseResponse>>>

    fun fetechTrendingWallpapers():Flow<Response<List<SingleDatabaseResponse>>>

    fun fetechCategoryWallpapers(cat:String):Flow<Response<List<SingleDatabaseResponse>>>

    fun fetechLiveWallpapers():Flow<Response<List<LiveWallpaperModel>>>

    fun getLiveWallpaperbyCategory(cat: String):Flow<Response<List<LiveWallpaperModel>>>


}