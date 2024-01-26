package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.FetchDataRepository
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetCategoryWallpapersUseCase @Inject constructor(private val fetchDataRepository: FetchDataRepository) {
    operator fun invoke(cat:String): Flow<Response<List<SingleDatabaseResponse>>> {
        return fetchDataRepository.fetechCategoryWallpapers(cat)
    }
}