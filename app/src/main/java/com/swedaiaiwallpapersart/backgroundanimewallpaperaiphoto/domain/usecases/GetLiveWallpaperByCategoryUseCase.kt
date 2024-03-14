package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.FetchDataRepository
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetLiveWallpaperByCategoryUseCase @Inject constructor(private val fetchDataRepository: FetchDataRepository) {
    operator fun invoke(cat:String): Flow<Response<List<LiveWallpaperModel>>> {
        return fetchDataRepository.getLiveWallpaperbyCategory(cat)
    }
}