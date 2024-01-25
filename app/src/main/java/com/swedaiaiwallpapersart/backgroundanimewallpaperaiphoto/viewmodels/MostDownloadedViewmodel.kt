package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.FetechAllWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetAllWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetTrendingWallpaperUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.MostDownloadImageResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.MostDownloadedImageModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.MostDownloadImages
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MostDownloadedViewmodel@Inject constructor(private val getAllWallpapersUsecase: GetAllWallpapersUsecase,
                                                 private val getTrendingWallpaperUseCase: GetTrendingWallpaperUseCase): ViewModel()  {
    val wallpaperData = MutableLiveData<ArrayList<CatResponse>?>()
    fun getWallpapers(): MutableLiveData<ArrayList<CatResponse>?> {
        return wallpaperData
    }
    fun fetchWallpapers(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            val retrofit = RetrofitInstance.getInstance()
            val service = retrofit.create(MostDownloadImages::class.java).postData(MySharePreference.getDeviceID(context)!!)

            service.enqueue(object : Callback<FavouriteListResponse> {

                override fun onResponse(
                    call: Call<FavouriteListResponse>,
                    response: Response<FavouriteListResponse>
                ) {
                    if(response.isSuccessful){

                        wallpaperData.value = response.body()?.images
                        Log.e("TAG", "onResponse: "+response.body()?.images )

//                        val rawJson = response.body()?.string()
//                        val gson = Gson()
//
//                        val responseData = gson.fromJson(rawJson, MostDownloadedImageModel::class.java)
//                        val images = responseData?.images?.toMutableList() ?: mutableListOf()
//
//                        Log.e("TAG", "onResponse: "+images )
//                        wallpaperData.value = ArrayList(images)


                    }else{
                        Log.e("TAG", "onResponse: not success" )
                    }
                }

                override fun onFailure(call: Call<FavouriteListResponse>, t: Throwable) {
                    Toast.makeText(context,
                        context.getString(R.string.error_loading_please_check_your_internet), Toast.LENGTH_SHORT).show()
                    Log.e("MostDownloaded", "onFailure: ${t.message}")
                }
            })
        }

    }



    private var _allCreations = MutableLiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<List<SingleDatabaseResponse>>>(
        com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(
            emptyList()
        ))
    val allCreations: LiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<List<SingleDatabaseResponse>>> = _allCreations

    fun getAllCreations(){
        viewModelScope.launch {
            getAllWallpapersUsecase.invoke().collect(){
                _allCreations.value=it
            }
        }
    }

    fun clearAllCreations(){
        _allCreations.value= com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(emptyList())
    }

    init {
            getAllCreations()

    }


    private var _trendingWallpapers = MutableLiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<List<SingleDatabaseResponse>>>(
        com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(
            emptyList()
        ))
    val trendingWallpapers: LiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<List<SingleDatabaseResponse>>> = _trendingWallpapers


    fun getAllTrendingWallpapers(){
        viewModelScope.launch {
            getTrendingWallpaperUseCase.invoke().collect(){
                _trendingWallpapers.value=it
            }
        }
    }

    fun clearAllTrending(){
        _allCreations.value= com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(emptyList())
    }
    fun clear(){
        wallpaperData.value = null
    }
}