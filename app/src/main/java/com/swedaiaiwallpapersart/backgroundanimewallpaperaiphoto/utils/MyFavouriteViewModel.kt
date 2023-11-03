package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieAnimationView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.FavouriteListInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFavouriteViewModel: ViewModel()  {
    private val wallpaperData = MutableLiveData<List<CatResponse>?>()
    fun getWallpapers(): MutableLiveData<List<CatResponse>?> {
        return wallpaperData
    }
    fun fetchWallpapers(context: Context,deviceId:String, animation: LottieAnimationView) {
        val retrofit = RetrofitInstance.getInstance()
        val service = retrofit.create(FavouriteListInterface::class.java).getList(MySharePreference.getDeviceID(context)!!)
        service.enqueue(object :Callback<FavouriteListResponse>{
            override fun onResponse(
                call: Call<FavouriteListResponse>,
                response: Response<FavouriteListResponse>
            ) {
               if(response.isSuccessful){
                   animation.visibility = View.INVISIBLE
                   wallpaperData.value = response.body()?.images
               }
            }
            override fun onFailure(call: Call<FavouriteListResponse>, t: Throwable) {
                animation.visibility = View.INVISIBLE
                Toast.makeText(context, "Error Loading", Toast.LENGTH_SHORT).show()
                Log.d("responseOk", "onResponse: response onFailure ")
            }
        })
    }
    fun clear(){
        wallpaperData.value = null
    }
}