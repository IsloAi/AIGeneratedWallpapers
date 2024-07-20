package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DeletedImagesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ResponseModelChargingAnimation
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ResponseModelDoubleWallpapers
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ResponseModelListMostDownloaded
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FeedbackModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveImagesResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

import retrofit2.http.Query

interface EndPointsInterface {

//    @GET("generateKey.php")
//    suspend fun generateDeviceToken(
//        @Query("deviceid") deviceId: String
//    ): retrofit2.Response<Unit>
//
//    @GET("all.php")
//    suspend fun getAllWallpapers(
//        @Header("Authorization") apiKey:String,
//        @Query("page") page:String,
//        @Query("record") record:String
//
//    ):retrofit2.Response<ListResponse>

    @GET("getupdatedwallpaper.php")
    suspend fun getUpdatedWallpapers(
        @Query("page") page: String,
        @Query("record") record: String,
        @Query("lastid") lastid:String
    ):retrofit2.Response<ListResponse>


    @GET("totalLikes.php")
    suspend fun getAllLikes(

    ):retrofit2.Response<ArrayList<LikesResponse>>

    @GET("get_mostdownloaded.php")
    suspend fun getMostUsed(
        @Query("page") page:String,
        @Query("record") record:String
    ):retrofit2.Response<ResponseModelListMostDownloaded>

    @GET("getLiveWallpaper.php")
    suspend fun getLiveWallpapers(
        @Query("page") page:String,
        @Query("record") record:String,
        @Query("deviceid") deviceId: String
    ):retrofit2.Response<LiveImagesResponse>

    @POST("post_downloads.php")
    suspend fun postDownloadedLive(
        @Body requestBody: Map<String, String?>
    )

    @GET("getfavrt.php")
    suspend fun getLiked(
        @Query("deviceid") deviceId: String
    ):retrofit2.Response<ArrayList<LikedResponse>>

    @POST("post_feedback.php")
    suspend fun postData(@Body data: FeedbackModel)


    @GET("getanimation.php")
    suspend fun getChargingAnimations(
    ):retrofit2.Response<ResponseModelChargingAnimation>


    @GET("getdoublewallpaper.php")
    suspend fun getDoubleWallpapers(
    ):retrofit2.Response<ResponseModelDoubleWallpapers>

    @GET("getStaticUpdates.php")
    suspend fun getStaticWallpaperUpdates():retrofit2.Response<ListResponse>

    @GET("getdeletedimages.php")
    suspend fun getDeletedImagesIDs():retrofit2.Response<ArrayList<DeletedImagesResponse>>
}