package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChargingAnimRemoteUseCase @Inject constructor(private val textToImgRepository: WallpaperRepositry){
    operator fun invoke(): Flow<Response<ArrayList<ChargingAnimModel>>> {
        return textToImgRepository.getChargingAnimation()
    }
}