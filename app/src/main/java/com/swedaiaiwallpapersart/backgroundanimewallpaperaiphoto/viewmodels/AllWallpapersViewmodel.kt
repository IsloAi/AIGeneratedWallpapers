package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetAllWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.AllWallpapers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import javax.inject.Inject

@HiltViewModel
class AllWallpapersViewmodel @Inject constructor(
    private val getAllWallpapersUsecase: GetAllWallpapersUsecase
) : ViewModel() {

    private val wallpaperData = MutableLiveData<ArrayList<CatResponse>?>()

    fun fetchWallpapers(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val retrofit = RetrofitInstance.getInstance()
            val service = retrofit.create(AllWallpapers::class.java).getList()
            service.enqueue(object : Callback<ListResponse> {
                override fun onResponse(
                    call: Call<ListResponse>,
                    response: retrofit2.Response<ListResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.e("AllViewmodel", "All Data: " + response.body())
                        //wallpaperData.value = response.body()?.images
                    }
                }

                override fun onFailure(call: Call<ListResponse>, t: Throwable) {
                    viewModelScope.launch(Dispatchers.Main) {

                        Toast.makeText(
                            context,
                            context.getString(R.string.error_loading_please_check_your_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }



                    Log.d("********Response fail", "onResponse: response onFailure ")
                }
            })

        }

    }

    fun clear() {
        wallpaperData.value = null
    }

}