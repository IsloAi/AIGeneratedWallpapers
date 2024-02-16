package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetLiveWallpapersUsecase @Inject constructor(private val textToImgRepository: WallpaperRepositry){
    operator fun invoke(page:String,record:String,deviceid:String): Flow<Response<ArrayList<LiveWallpaperModel>>> {
        return textToImgRepository.getLiveWallpapers(page,record,deviceid)
    }
}