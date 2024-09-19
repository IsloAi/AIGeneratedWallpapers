package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.RewardedAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetRewardWallpaperUseCase @Inject constructor(private val textToImgRepository: WallpaperRepositry){
    operator fun invoke(): Flow<Response<ArrayList<RewardedAllResponse>>> {
        return textToImgRepository.getRewardWallpaper()
    }
}