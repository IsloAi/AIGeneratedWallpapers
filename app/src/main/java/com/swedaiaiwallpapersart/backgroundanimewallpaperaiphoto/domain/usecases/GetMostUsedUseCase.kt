package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.MostDownloadedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMostUsedUseCase @Inject constructor(private val textToImgRepository: WallpaperRepositry){
    operator fun invoke(page:String,record:String): Flow<Response<ArrayList<MostDownloadedResponse>>> {
        return textToImgRepository.getMostDownloaded(page,record)
    }
}