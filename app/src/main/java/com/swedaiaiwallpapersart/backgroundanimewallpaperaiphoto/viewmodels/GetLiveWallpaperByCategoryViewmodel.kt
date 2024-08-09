package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetLiveWallpaperByCategoryUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetLiveWallpaperFromDbUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetLiveWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetLiveWallpaperByCategoryViewmodel@Inject constructor(private val getLiveWallpaperByCategoryUseCase: GetLiveWallpaperByCategoryUseCase): ViewModel()  {


    private var _liveWallpapers= MutableLiveData<Response<List<LiveWallpaperModel>>>(
        Response.Success(null))
    val liveWallpapers: LiveData<Response<List<LiveWallpaperModel>>> = _liveWallpapers


    fun getMostUsed(page:String){
        viewModelScope.launch {
            getLiveWallpaperByCategoryUseCase.invoke(page).collect(){
                Log.e("TAG", "getAllModels: $it")
                _liveWallpapers.value=it
            }
        }
    }

}