package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieAnimationView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetAllLikedUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetAllLikesUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetAllWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetTrendingWallpaperUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.HomeListInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.HomeListInterfaceWithId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MyHomeViewModel@Inject constructor(private val getTrendingWallpaperUseCase: GetTrendingWallpaperUseCase, private val getAllLikesUsecase: GetAllLikesUsecase,private val getAllLikedUseCase: GetAllLikedUseCase
): ViewModel()  {
    val wallpaperData = MutableLiveData<ArrayList<CatResponse>?>()

    private val _networkRequestStatus = MutableLiveData<Boolean>()
    val networkRequestStatus: LiveData<Boolean> get() = _networkRequestStatus
    fun getWallpapers(): MutableLiveData<ArrayList<CatResponse>?> {
        return wallpaperData
    }
    fun fetchWallpapers(context: Context, animation: LottieAnimationView,page:String) {
        clear()

        viewModelScope.launch(Dispatchers.IO) {

            val retrofit = RetrofitInstance.getInstance()
            val service = retrofit.create(HomeListInterfaceWithId::class.java).getList(MySharePreference.getDeviceID(context)!!,page)

            service.enqueue(object :Callback<FavouriteListResponse>{
                override fun onResponse(
                    call: Call<FavouriteListResponse>, response: Response<FavouriteListResponse>) {
                    if(response.isSuccessful){
                        viewModelScope.launch(Dispatchers.Main) {
                            animation.visibility = View.INVISIBLE
                        }

                        wallpaperData.value = response.body()?.images
                    }

                    _networkRequestStatus.value = false
                }
                override fun onFailure(call: Call<FavouriteListResponse>, t: Throwable) {
                    viewModelScope.launch(Dispatchers.Main) {
                        animation.visibility = View.INVISIBLE
                        Toast.makeText(context,
                            context.getString(R.string.error_loading_please_check_your_internet), Toast.LENGTH_SHORT).show()
                    }
                    _networkRequestStatus.value = false


                    Log.d("responseOk", "onResponse: response onFailure ")
                }
            })

        }

    }
    fun clear(){
        wallpaperData.value = null
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


    private var _allLikes = MutableLiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<ArrayList<LikesResponse>>>(
        com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(
            arrayListOf()
        ))
    val allLikes: LiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<ArrayList<LikesResponse>>> = _allLikes
    fun getAllLikes(){
        viewModelScope.launch {
            getAllLikesUsecase.invoke().collect(){
                _allLikes.value=it
            }
        }
    }



    private var _allLiked = MutableLiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<ArrayList<LikedResponse>>>(
        com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(
            arrayListOf()
        ))
    val allLiked: LiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<ArrayList<LikedResponse>>> = _allLiked
    fun getAllLiked(device:String){
        viewModelScope.launch {
            getAllLikedUseCase.invoke(device).collect(){
                _allLiked.value=it
            }
        }
    }
}