package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repositry

import android.util.Log
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.FetchDataRepository
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ForegroundWorker.Companion.TAG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class FetchDataRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : FetchDataRepository {

    override fun fetechAllWallpapers(): Flow<Response<List<SingleDatabaseResponse>>> = channelFlow {
        try {

            trySend(Response.Loading)

            val creations= appDatabase.wallpapersDao().getAllWallpapers()
            if (creations.isNotEmpty()){
                trySend(Response.Success(creations))
            }else{
                trySend(Response.Error("No Data found"))
            }

        }
        catch (e:Exception){
            trySend(Response.Error("Unexpected error ${e.message}"))
        }
        awaitClose()
    }

    override fun fetechTrendingWallpapers(): Flow<Response<List<SingleDatabaseResponse>>> = channelFlow {
        try {

            trySend(Response.Loading)

            val creations= appDatabase.wallpapersDao().getTrendingWallpapers()
            if (creations.isNotEmpty()){
                trySend(Response.Success(creations))
            }else{
                trySend(Response.Error("No Data found"))
            }

        }
        catch (e:Exception){
            trySend(Response.Error("Unexpected error ${e.message}"))
        }
        awaitClose()
    }

    override fun fetechCategoryWallpapers(cat: String): Flow<Response<List<SingleDatabaseResponse>>> = channelFlow {
        try {

            trySend(Response.Loading)

            val creations= appDatabase.wallpapersDao().getCategoryWallpaper(cat)
            if (creations.isNotEmpty()){
                trySend(Response.Success(creations))
            }else{
                trySend(Response.Error("No Data found"))
            }

        }
        catch (e:Exception){
            trySend(Response.Error("Unexpected error ${e.message}"))
        }
        awaitClose()
    }

    override fun fetechLiveWallpapers(): Flow<Response<List<LiveWallpaperModel>>> =
        appDatabase.liveWallpaperDao().getAllWallpapers()
            .map { creations ->
                Log.d(TAG, "fetechLiveWallpapers: ${creations.size}")
                if (creations.isNotEmpty()) {
                    Response.Success(creations)
                } else {
                    Response.Error("No Data found")
                }
            }
            .catch { e ->
                emit(Response.Error("Unexpected error ${e.message}"))
            }

    override fun getLiveWallpaperbyCategory(cat: String): Flow<Response<List<LiveWallpaperModel>>> = channelFlow {
        try {
            trySend(Response.Loading)

            val creations= appDatabase.liveWallpaperDao().getCatgoriesWallpapers(cat)
            if (creations.isNotEmpty()){
                trySend(Response.Success(creations))
            }else{
                trySend(Response.Error("No Data found"))
            }

        }
        catch (e:Exception){
            trySend(Response.Error("Unexpected error ${e.message}"))
        }
        awaitClose()
    }
}