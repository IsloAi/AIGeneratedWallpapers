package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repository

import android.util.Log
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.endpoints.APIEndpoints
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.ChargingAnimationResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.DoubleApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.LiveApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.StaticApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CategoryApiModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository.WallpaperAPIRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.Response
import java.net.UnknownHostException
import javax.inject.Inject

class WallpaperAPIRepositoryImplementation @Inject constructor(
    private val endpoints: APIEndpoints
) : WallpaperAPIRepository {

    override suspend fun getAllWallpapers(): Flow<Response<StaticApiResponse>> =
        channelFlow {
            try {
                val resp = endpoints.getAllStaticWallpapers()
                if (resp.isSuccessful) {
                    //Log.d("TAG", "GetAllStaticWallpapers: ${resp.body()}")
                    send(resp)
                }
            } catch (e: Exception) {
                Log.d("TAG", "GetAllStaticWallpapers Error: ${e.message}")
            }
        }

    override suspend fun getLiveWallpaper(): Flow<Response<LiveApiResponse>> =
        channelFlow {
            try {
                val resp = endpoints.getLiveWallpapers()
                if (resp.isSuccessful) {
                    Log.d("TAG", "getLiveWallpaper: ${resp.body()}")
                    send(resp)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }
        }

    override fun getChargingAnimation(): Flow<Response<ChargingAnimationResponse>> =
        channelFlow {
            try {
                val resp = endpoints.getChargingAnimations()
                if (resp.isSuccessful) {
                    //Log.d("TAG", "getChargingAnimation: ${resp.body()}")
                    send(resp)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }
        }

    override fun getDoubleWallpapers(): Flow<Response<DoubleApiResponse>> = channelFlow {
        try {
            val resp = endpoints.getDoubleWallpapers()
            if (resp.isSuccessful) {
                //Log.d("TAG", "getDoubleWallpapers: ${resp.body()}")
                send(resp)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
    }

    override fun getCategories(): Flow<Response<List<CategoryApiModel>>> = channelFlow {
        try {
            val resp = endpoints.getCategories()
            if (resp.isSuccessful) {
                Log.d("Repo", "✅ getCategories success: ${resp.body()}")
                send(resp)
            } else {
                Log.e("Repo", "❌ getCategories failed: ${resp.errorBody()?.string()}")
                send(resp) // Still send to emit the error response
            }
        } catch (e: Exception) {
            Log.e("Repo", "❌ Exception in getCategories: ${e.message}", e)
            // Optional: you can emit a dummy error response
            // send(Response.error(...)) if you want to show something upstream
        }
    }

}