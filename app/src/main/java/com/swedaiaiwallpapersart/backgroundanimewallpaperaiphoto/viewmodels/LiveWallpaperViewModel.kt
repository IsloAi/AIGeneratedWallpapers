package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveImagesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.GetLiveWallpapers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveWallpaperViewModel: ViewModel()  {
    val wallpaperData = MutableLiveData<ArrayList<LiveWallpaperModel>?>()


    fun getWallpapers(): MutableLiveData<ArrayList<LiveWallpaperModel>?> {
        return wallpaperData
    }
    fun fetchWallpapers(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {

            val retrofit = RetrofitInstance.getInstance()
            val service = retrofit.create(GetLiveWallpapers::class.java).getLiveWallpapers()

            service.enqueue(object : Callback<LiveImagesResponse> {
                override fun onResponse(
                    call: Call<LiveImagesResponse>, response: Response<LiveImagesResponse>
                ) {
                    if(response.isSuccessful){
                        wallpaperData.value = response.body()?.images
                    }
                }
                override fun onFailure(call: Call<LiveImagesResponse>, t: Throwable) {

                    viewModelScope.launch(Dispatchers.Main) {

                        Toast.makeText(context,
                            context.getString(R.string.error_loading_please_check_your_internet), Toast.LENGTH_SHORT).show()
                        Log.d("responseOk", "onResponse: response onFailure ")

                    }

                }
            })


        }

    }
    fun clear(){
        wallpaperData.value = null
    }
}