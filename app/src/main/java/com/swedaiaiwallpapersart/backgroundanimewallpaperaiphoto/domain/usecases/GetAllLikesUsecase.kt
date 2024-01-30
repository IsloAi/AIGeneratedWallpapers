package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.TokenResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetAllLikesUsecase @Inject constructor(private val textToImgRepository: WallpaperRepositry){
    operator fun invoke(): Flow<Response<ArrayList<LikesResponse>>> {
        return textToImgRepository.getAllLikes()
    }
}