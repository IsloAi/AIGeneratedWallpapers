package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.MostDownloadedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetLiveWallpaperFromDbUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetLiveWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveImagesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.GetLiveWallpapers
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LiveWallpaperViewModel@Inject constructor(private val getLiveWallpapersUsecase: GetLiveWallpapersUsecase,private val getLiveWallpaperFromDbUsecase: GetLiveWallpaperFromDbUsecase): ViewModel()  {


    private var _liveWallpapers= MutableLiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<ArrayList<LiveWallpaperModel>>>(
        com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(null))
    val liveWallpapers: LiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<ArrayList<LiveWallpaperModel>>> = _liveWallpapers


    fun getMostUsed(page:String,record:String,deviceid:String){
        viewModelScope.launch {
            getLiveWallpapersUsecase.invoke(page,record,deviceid).collect(){
                Log.e("TAG", "getAllModels: "+it )
                _liveWallpapers.value=it
            }
        }
    }


    private var _liveWallsFromDb= MutableLiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<List<LiveWallpaperModel>>>(
        com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(null))
    val liveWallsFromDB: LiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<List<LiveWallpaperModel>>> = _liveWallsFromDb


    fun getAllTrendingWallpapers(){
        viewModelScope.launch {
            getLiveWallpaperFromDbUsecase.invoke().collect(){
                _liveWallsFromDb.value=it
            }
        }
    }

}