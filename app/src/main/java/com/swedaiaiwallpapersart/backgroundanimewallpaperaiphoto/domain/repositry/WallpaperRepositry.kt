package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DeletedImagesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.MostDownloadedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.TokenResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow


interface WallpaperRepositry {
//    fun GenerateDeviceToken(deviceId: String): Flow<Response<TokenResponse>>
//
//    fun getAllWallpapers(apiKey:String,page:String,record:String):Flow<Response<ArrayList<SingleAllResponse>>>

    fun getUpdatedWallpapers(page:String,record:String,lastid:String):Flow<Response<ArrayList<SingleAllResponse>>>



    fun getAllLikes():Flow<Response<ArrayList<LikesResponse>>>
    fun getLiked(deviceId: String):Flow<Response<ArrayList<LikedResponse>>>

    fun getMostDownloaded(page:String,record:String):Flow<Response<ArrayList<MostDownloadedResponse>>>



    fun getLiveWallpapers(page:String,record:String,deviceId: String):Flow<Response<ArrayList<LiveWallpaperModel>>>


    fun getChargingAnimation():Flow<Response<ArrayList<ChargingAnimModel>>>


    fun getDoubleWallpapers():Flow<Response<ArrayList<DoubleWallModel>>>

    fun getStaticWallpaperUpdates():Flow<Response<ArrayList<SingleAllResponse>>>

    fun getDeletedImages():Flow<Response<ArrayList<DeletedImagesResponse>>>


}