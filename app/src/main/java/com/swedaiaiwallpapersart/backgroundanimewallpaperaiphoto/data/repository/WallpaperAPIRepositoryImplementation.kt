package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repository

import android.util.Log
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.ChargingAnimModel
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.DoubleWallModel
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.endpoints.APIEndpoints
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.staticApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository.WallpaperAPIRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.Response
import java.net.UnknownHostException
import javax.inject.Inject

class WallpaperAPIRepositoryImplementation @Inject constructor(
    private val endpoints: APIEndpoints
) : WallpaperAPIRepository {

    override suspend fun getAllWallpapers(): Flow<Response<staticApiResponse>> =
        channelFlow {
            try {
                val resp = endpoints.getAllStaticWallpapers()
                if (resp.isSuccessful) {
                    Log.d("TAG", "GetAllStaticWallpapers: ${resp.body()}")
                    send(resp)
                }
            } catch (e: Exception) {
                Log.d("TAG", "GetAllStaticWallpapers Error: ${e.message}")
            }
        }

    override suspend fun getLiveWallpaper(): Flow<Response<ArrayList<LiveWallpaperModel>>> =
        channelFlow {
            try {
                val resp = endpoints.getLiveWallpapers()
                if (resp.isSuccessful) {
                    Log.d("TAG", "getLiveWallpaper: ${resp.body()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }
        }

    override fun getChargingAnimation(): Flow<Response<ArrayList<ChargingAnimModel>>> =
        channelFlow {
            try {
                val resp = endpoints.getChargingAnimations()
                if (resp.isSuccessful) {
                    Log.d("TAG", "getChargingAnimation: ${resp.body()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }
        }

    override fun getDoubleWallpapers(): Flow<Response<ArrayList<DoubleWallModel>>> = channelFlow {
        try {
            val resp = endpoints.getDoubleWallpapers()
            if (resp.isSuccessful) {
                Log.d("TAG", "getDoubleWallpapers: ${resp.body()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
    }

}