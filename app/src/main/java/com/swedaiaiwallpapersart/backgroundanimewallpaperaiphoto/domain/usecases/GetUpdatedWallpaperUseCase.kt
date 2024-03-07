package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetUpdatedWallpaperUseCase @Inject constructor(private val textToImgRepository: WallpaperRepositry){
    operator fun invoke(page:String,record:String,lastid:String): Flow<Response<ArrayList<SingleAllResponse>>> {
        return textToImgRepository.getUpdatedWallpapers(page,record,lastid)
    }
}