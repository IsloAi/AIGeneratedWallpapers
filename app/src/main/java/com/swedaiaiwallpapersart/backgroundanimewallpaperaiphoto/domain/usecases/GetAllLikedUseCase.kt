package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetAllLikedUseCase @Inject constructor(private val textToImgRepository: WallpaperRepositry){
    operator fun invoke(deviceId: String): Flow<Response<ArrayList<LikedResponse>>> {
        return textToImgRepository.getLiked(deviceId)
    }
}