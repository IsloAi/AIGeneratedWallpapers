package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.LiveApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository.WallpaperAPIRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

//Created on  by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

class GetLiveWallpapersUseCase @Inject constructor(private val repository: WallpaperAPIRepository) {
    suspend operator fun invoke(): Flow<Response<LiveApiResponse>> {
        return repository.getLiveWallpaper()
    }
}