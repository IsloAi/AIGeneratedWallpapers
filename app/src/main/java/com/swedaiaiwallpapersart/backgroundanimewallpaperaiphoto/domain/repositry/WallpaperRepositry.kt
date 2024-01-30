package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.TokenResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow


interface WallpaperRepositry {
    fun GenerateDeviceToken(deviceId: String): Flow<Response<TokenResponse>>

    fun getAllWallpapers(apiKey:String,page:String,record:String):Flow<Response<ArrayList<SingleAllResponse>>>

    fun getAllLikes():Flow<Response<ArrayList<LikesResponse>>>
    fun getLiked(deviceId: String):Flow<Response<ArrayList<LikedResponse>>>
}