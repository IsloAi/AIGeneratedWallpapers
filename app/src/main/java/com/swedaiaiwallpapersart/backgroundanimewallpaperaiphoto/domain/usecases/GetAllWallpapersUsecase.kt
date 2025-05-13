package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.apiResponse.StaticApiResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository.WallpaperAPIRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

//Created on  by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

class GetAllWallpapersUseCase @Inject constructor(private val repository: WallpaperAPIRepository) {
    suspend operator fun invoke(): Flow<Response<StaticApiResponse>> {
        return repository.getAllWallpapers()
    }
}