package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository.WallpaperAPIRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

//Created on  by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

class GetDoubleWallpaperUseCase @Inject constructor(private val repository: WallpaperAPIRepository) {
    operator fun invoke(): Flow<Response<ArrayList<DoubleWallModel>>> {
        return repository.getDoubleWallpapers()
    }
}