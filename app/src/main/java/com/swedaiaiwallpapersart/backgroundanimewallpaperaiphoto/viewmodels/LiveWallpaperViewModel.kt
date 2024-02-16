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
//    val wallpaperData = MutableLiveData<ArrayList<LiveWallpaperModel>?>()
//
//    val TAG = "LIVEMODEL"
//
//
//    fun getWallpapers(): MutableLiveData<ArrayList<LiveWallpaperModel>?> {
//        return wallpaperData
//    }

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

//    fun fetchWallpapers(context: Context) {
//
//        viewModelScope.launch(Dispatchers.IO) {
//
//            val retrofit = RetrofitInstance.getInstance()
//            val service = retrofit.create(GetLiveWallpapers::class.java).getLiveWallpapers(
//                MySharePreference.getDeviceID(context)!!
//            )
//
//            service.enqueue(object : Callback<LiveImagesResponse> {
//                override fun onResponse(
//                    call: Call<LiveImagesResponse>, response: Response<LiveImagesResponse>
//                ) {
//                    if(response.isSuccessful){
//                        Log.e(TAG, "onResponse: "+response.body()?.images )
//                        wallpaperData.value = response.body()?.images
//                    }
//                }
//                override fun onFailure(call: Call<LiveImagesResponse>, t: Throwable) {
//
//                    viewModelScope.launch(Dispatchers.Main) {
//
//                        Toast.makeText(context,
//                            context.getString(R.string.error_loading_please_check_your_internet), Toast.LENGTH_SHORT).show()
//                        Log.d(TAG, "onResponse: response onFailure ")
//
//                    }
//
//                }
//            })
//
//
//        }
//
//    }
//    fun clear(){
//        wallpaperData.value = null
//    }
}