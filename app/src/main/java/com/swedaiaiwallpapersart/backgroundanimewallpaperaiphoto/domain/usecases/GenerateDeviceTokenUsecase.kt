package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.TokenResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateDeviceTokenUsecase @Inject constructor(private val textToImgRepository: WallpaperRepositry){
//    operator fun invoke(deviceId: String): Flow<Response<TokenResponse>> {
//        return textToImgRepository.GenerateDeviceToken(deviceId)
//    }
}