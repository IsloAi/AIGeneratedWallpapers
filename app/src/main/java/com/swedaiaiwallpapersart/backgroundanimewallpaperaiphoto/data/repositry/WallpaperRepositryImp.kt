package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repositry

import android.util.Log
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DeletedImagesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.MostDownloadedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.TokenResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.net.UnknownHostException
import javax.inject.Inject


class WallpaperRepositryImp@Inject constructor(
    private val webApiInterface: EndPointsInterface,
):WallpaperRepositry {

    override fun getUpdatedWallpapers(
        page: String,
        record: String,
        lastid: String
    ): Flow<Response<ArrayList<SingleAllResponse>>> = channelFlow {

        try {
            trySend(Response.Loading)
            Log.e("TAG", "GenerateTextToImage: I came here")
            val resp = webApiInterface.getUpdatedWallpapers(page,record,lastid)
            Log.e("TAG", "GenerateTextToImage: $resp")

            if (resp.isSuccessful){

                trySend(Response.Success(resp.body()?.images))
            }



            Log.e("TAG", "getAllWallpapers: " )

        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        } catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }

        awaitClose()

    }


    override fun getAllLikes(): Flow<Response<ArrayList<LikesResponse>>>  = channelFlow {
        try {
            trySend(Response.Loading)

            val resp = webApiInterface.getAllLikes()
            if (resp.isSuccessful){
                val list = ArrayList<LikesResponse>()
                val likesList = resp.body() ?: emptyList<LikesResponse>()



                trySend(Response.Success(resp.body()))
                Log.e("TAG", "getAllLikes: "+resp.body())
            }
        }catch (e:Exception){

        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }
    }

    override fun getLiked(deviceId: String): Flow<Response<ArrayList<LikedResponse>>> = channelFlow {
        try {
            trySend(Response.Loading)
            val resp = webApiInterface.getLiked(deviceId)

            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()))
            }


        }catch (e:Exception){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }
    }

    override fun getMostDownloaded(
        page: String,
        record: String
    ): Flow<Response<ArrayList<MostDownloadedResponse>>> = channelFlow {

        try {
            trySend(Response.Loading)
            Log.e("TAG", "GenerateTextToImage: I came here")
            val resp = webApiInterface.getMostUsed(page,record)
            Log.e("TAG", "GenerateTextToImage: $resp")

            if (resp.isSuccessful){

                trySend(Response.Success(resp.body()?.images))
            }
            Log.e("TAG", "getAllWallpapers: " )
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
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
            Log.e("TAG", "GenerateTextToImage: I came here")
            val resp = webApiInterface.getLiveWallpapers(page,record,deviceId)
            Log.e("TAG", "GenerateTextToImage: $resp")

            if (resp.isSuccessful){

                trySend(Response.Success(resp.body()?.images))
            }
            Log.e("TAG", "getAllWallpapers: " )
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }

        awaitClose()

    }

    override fun getChargingAnimation(): Flow<Response<ArrayList<ChargingAnimModel>>> = channelFlow {

        try {
            trySend(Response.Loading)
            Log.e("TAG", "getChargingAnimation: I came here")
            val resp = webApiInterface.getChargingAnimations()
            Log.e("TAG", "getChargingAnimation: $resp")

            if (resp.isSuccessful){

                trySend(Response.Success(resp.body()?.images))
            }
            Log.e("TAG", "getChargingAnimation: " )
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }

        awaitClose()

    }

    override fun getDoubleWallpapers(): Flow<Response<ArrayList<DoubleWallModel>>>  = channelFlow {

        try {
            trySend(Response.Loading)
            Log.e("TAG", "getChargingAnimation: I came here")
            val resp = webApiInterface.getDoubleWallpapers()
            Log.e("TAG", "getChargingAnimation: $resp")

            if (resp.isSuccessful){

                trySend(Response.Success(resp.body()?.images))
            }
            Log.e("TAG", "getChargingAnimation: " )
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }

        awaitClose()

    }

    override fun getStaticWallpaperUpdates(): Flow<Response<ArrayList<SingleAllResponse>>> = channelFlow {

        try {
            trySend(Response.Loading)
            Log.e("TAG", "GenerateTextToImage: I came here")
            val resp = webApiInterface.getStaticWallpaperUpdates()
            Log.e("TAG", "GenerateTextToImage: $resp")

            if (resp.isSuccessful){

                trySend(Response.Success(resp.body()?.images))
            }



            Log.e("TAG", "getAllWallpapers: " )

        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }

        awaitClose()

    }

    override fun getDeletedImages(): Flow<Response<ArrayList<DeletedImagesResponse>>> = channelFlow {
        try {
            trySend(Response.Loading)

            val resp = webApiInterface.getDeletedImagesIDs()
            if (resp.isSuccessful){
                trySend(Response.Success(resp.body()))
                Log.e("TAG", "getDeletedImages: "+resp.body())
            }else{
                Log.e("TAG", "getDeletedImages: failed"+resp.message() )
            }
        }catch (e:Exception){
            e.printStackTrace()
        }catch (e:UnknownHostException){
            e.printStackTrace()
            trySend(Response.Error("unexpected error occoured ${e.message}"))
        }
    }


}