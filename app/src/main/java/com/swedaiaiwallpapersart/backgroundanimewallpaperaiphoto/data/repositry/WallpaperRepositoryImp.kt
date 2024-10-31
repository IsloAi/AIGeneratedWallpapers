package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repositry

import android.util.Log
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DeletedImagesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.MostDownloadedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.RewardedAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.net.UnknownHostException
import javax.inject.Inject


class WallpaperRepositoryImp@Inject constructor(
    private val webApiInterface: EndPointsInterface,
):WallpaperRepositry {

    override fun getUpdatedWallpapers(
        page: String,
        record: String,
        lastid: String
    ): Flow<Response<ArrayList<SingleAllResponse>>> = channelFlow {

        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getUpdatedWallpapers(page,record,lastid)
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()?.images))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        } catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }
        awaitClose()
    }

    override fun getRewardWallpaper(): Flow<Response<ArrayList<RewardedAllResponse>>> = channelFlow {
        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getRewardWallpaper()

            if (resp.isSuccessful){
                Log.e("TAG", "GenerateTextToImage: ${resp.body()?.images}")
                trySend(Response.Success(resp.body()?.images))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        } catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }

        awaitClose()
    }

    override fun getAllLikes(): Flow<Response<ArrayList<LikesResponse>>>  = channelFlow {
        try {
            trySend(Response.Loading)

            val resp = webApiInterface.getAllLikes()
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()))
            }
        }catch (e:Exception){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }
    }

    override fun getFavourites(deviceId: String): Flow<Response<ArrayList<LikedResponse>>> = channelFlow {
        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getFavourite(deviceId)
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()))
            }
        }catch (e:Exception){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }
    }

    override fun getMostDownloaded(
        page: String,
        record: String
    ): Flow<Response<ArrayList<MostDownloadedResponse>>> = channelFlow {
        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getMostUsed(page,record)
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()?.images))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }
        awaitClose()
    }

    override fun getLiveWallpapers(
        page: String,
        record: String,
        deviceId: String
    ): Flow<Response<ArrayList<LiveWallpaperModel>>> = channelFlow {

        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getLiveWallpapers(page,record,deviceId)
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()?.images))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }

        awaitClose()

    }

    override fun getChargingAnimation(): Flow<Response<ArrayList<ChargingAnimModel>>> = channelFlow {

        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getChargingAnimations()
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()?.images))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }
        awaitClose()
    }

    override fun getDoubleWallpapers(): Flow<Response<ArrayList<DoubleWallModel>>>  = channelFlow {

        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getDoubleWallpapers()
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()?.images))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }
        awaitClose()
    }

    override fun getStaticWallpaperUpdates(): Flow<Response<ArrayList<SingleAllResponse>>> = channelFlow {

        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getStaticWallpaperUpdates()
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()?.images))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }

        awaitClose()

    }

    override fun getDeletedImages(): Flow<Response<ArrayList<DeletedImagesResponse>>> = channelFlow {
        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getDeletedImagesIDs()
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()))
            }
        }catch (e:Exception){
            e.printStackTrace()
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occurred ${e.message}"))
        }
    }


}