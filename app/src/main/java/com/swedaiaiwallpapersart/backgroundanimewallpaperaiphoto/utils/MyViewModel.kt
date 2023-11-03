package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieAnimationView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ListResponseInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ListResponseInterfaceNoUid
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyViewModel: ViewModel()  {
    private val wallpaperData = MutableLiveData<List<CatResponse>?>()
    fun getWallpapers(): MutableLiveData<List<CatResponse>?> {
        return wallpaperData
    }
    fun fetchWallpapers(
        context: Context,
        catName: String,
        animation: LottieAnimationView,
        isLogin: Boolean
    ) {
        val retrofit = RetrofitInstance.getInstance()
        val service = if (isLogin){
           retrofit.create(ListResponseInterface::class.java).getList(catName,MySharePreference.getDeviceID(context)!!)
        } else {
            retrofit.create(ListResponseInterfaceNoUid::class.java).getList(catName)
        }



        service.enqueue(object : Callback<List<CatResponse>> {
            override fun onResponse(call: Call<List<CatResponse>>, response: Response<List<CatResponse>>) {
                if (response.isSuccessful) {
                    animation.visibility = View.INVISIBLE
                    val catResponses: List<CatResponse>? = response.body()
                    if (catResponses != null) {
                        Log.d("responseOk", "onResponse: response  "+catResponses)
                        wallpaperData.value = catResponses
                    }
                } else {
                    // Handle error case
                    Log.d("responseOk", "onResponse: response not Empty ")
                }
            }

            override fun onFailure(call: Call<List<CatResponse>>, t: Throwable) {
                animation.visibility = View.INVISIBLE
                Toast.makeText(context, "Error Loading", Toast.LENGTH_SHORT).show()
                // Handle failure case
                Log.d("responseOk", "onResponse: response onFailure ")
            }

        })
    }
    fun clear(){
        wallpaperData.value = null
    }
}