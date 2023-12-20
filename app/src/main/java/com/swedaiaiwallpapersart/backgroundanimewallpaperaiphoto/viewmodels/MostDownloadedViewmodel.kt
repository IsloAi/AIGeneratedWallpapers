package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.MostDownloadImageResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.MostDownloadedImageModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.MostDownloadImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MostDownloadedViewmodel: ViewModel()  {
    val wallpaperData = MutableLiveData<ArrayList<MostDownloadImageResponse?>?>()
    fun getWallpapers(): MutableLiveData<ArrayList<MostDownloadImageResponse?>?> {
        return wallpaperData
    }
    fun fetchWallpapers(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            val retrofit = RetrofitInstance.getInstance()
            val service = retrofit.create(MostDownloadImages::class.java).postData()

            service.enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        val rawJson = response.body()?.string()
                        val gson = Gson()

                        val responseData = gson.fromJson(rawJson, MostDownloadedImageModel::class.java)
                        val images = responseData?.images?.toMutableList() ?: mutableListOf()

                        Log.e("TAG", "onResponse: "+images )
                        wallpaperData.value = ArrayList(images)
                    }else{
                        Log.e("TAG", "onResponse: not success" )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context,
                        context.getString(R.string.error_loading_please_check_your_internet), Toast.LENGTH_SHORT).show()
                    Log.e("TAG", "onResponse: failed" )
                }
            })
        }

    }
    fun clear(){
        wallpaperData.value = null
    }
}