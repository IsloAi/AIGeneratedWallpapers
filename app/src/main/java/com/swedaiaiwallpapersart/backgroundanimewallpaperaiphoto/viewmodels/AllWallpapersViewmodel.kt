package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.AllWallpapers
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllWallpapersViewmodel: ViewModel()  {
    val wallpaperData = MutableLiveData<ArrayList<CatResponse>?>()
    fun getWallpapers(): MutableLiveData<ArrayList<CatResponse>?> {
        return wallpaperData
    }
    fun fetchWallpapers(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {

            val retrofit = RetrofitInstance.getInstance()
            val service = retrofit.create(AllWallpapers::class.java).getList(
                "jhkjhsda324")

            service.enqueue(object :Callback<FavouriteListResponse>{
                override fun onResponse(
                    call: Call<FavouriteListResponse>, response: Response<FavouriteListResponse>) {
                    if(response.isSuccessful){

                        Log.e("TAG", "initSearchData: "+response.body()?.images )


                     wallpaperData.value = response.body()?.images
                    }


                }
                override fun onFailure(call: Call<FavouriteListResponse>, t: Throwable) {
                    viewModelScope.launch(Dispatchers.Main) {

                        Toast.makeText(context,
                            context.getString(R.string.error_loading_please_check_your_internet), Toast.LENGTH_SHORT).show()
                    }



                    Log.d("********Response fail", "onResponse: response onFailure ")
                }
            })

        }

    }
    fun clear(){
        wallpaperData.value = null
    }
}