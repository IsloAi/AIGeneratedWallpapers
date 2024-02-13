package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repositry

import android.util.Log
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.MostDownloadedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.TokenResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject


class WallpaperRepositryImp@Inject constructor(
    private val webApiInterface: EndPointsInterface,
):WallpaperRepositry {

    override fun GenerateDeviceToken(deviceId: String): Flow<Response<TokenResponse>> = channelFlow {
        try {
            trySend(Response.Loading)
            val resp = webApiInterface.generateDeviceToken(deviceId = deviceId)


            if (resp.isSuccessful){
                val header = resp.headers()



                val apiKey: String? = header["x-api-key"]
                trySend(Response.Success(TokenResponse(apiKey!!)))



                Log.e("TAG", "GenerateDeviceToken: $apiKey")

            }else{
                Log.e("TAG", "GenerateDeviceToken: not success", )
            }


        } catch (e: Exception) {
            Log.e("TAG", "GenerateDeviceToken: "+e.message )
        }
        awaitClose()
    }

    override fun getAllWallpapers(
        apiKey: String,
        page: String,
        record: String
    ): Flow<Response<ArrayList<SingleAllResponse>>> = channelFlow {

        try {
            trySend(Response.Loading)
            Log.e("TAG", "GenerateTextToImage: I came here")
            val resp = webApiInterface.getAllWallpapers(apiKey,page,record)
            Log.e("TAG", "GenerateTextToImage: $resp")

            if (resp.isSuccessful){

                trySend(Response.Success(resp.body()?.images))
            }



            Log.e("TAG", "getAllWallpapers: " )

        } catch (e: Exception) {
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
        }

        awaitClose()

    }
}