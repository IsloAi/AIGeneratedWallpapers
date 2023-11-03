package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieAnimationView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.HomeListInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.HomeListInterfaceWithId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyHomeViewModel: ViewModel()  {
    private val wallpaperData = MutableLiveData<ArrayList<CatResponse>?>()
    fun getWallpapers(): MutableLiveData<ArrayList<CatResponse>?> {
        return wallpaperData
    }
    fun fetchWallpapers(context: Context, animation: LottieAnimationView, isLogin: Boolean) {
        val retrofit = RetrofitInstance.getInstance()
        val service = if (isLogin) {
            retrofit.create(HomeListInterfaceWithId::class.java).getList(MySharePreference.getDeviceID(context)!!)
        } else {
            retrofit.create(HomeListInterface::class.java).getList()
        }
        service.enqueue(object :Callback<FavouriteListResponse>{
            override fun onResponse(
                call: Call<FavouriteListResponse>, response: Response<FavouriteListResponse>) {
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