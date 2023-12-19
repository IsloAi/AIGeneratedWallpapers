package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.temp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitPixabay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class PixabayResponseViewModel : ViewModel() {
    private val _wallpaperData = MutableLiveData<List<VideoItem>?>()
    val wallpaperData: LiveData<List<VideoItem>?> get() = _wallpaperData

    private val _networkRequestStatus = MutableLiveData<Boolean>()
    val networkRequestStatus: LiveData<Boolean> get() = _networkRequestStatus

    fun fetchWallpapers() {
        _networkRequestStatus.value = true
        val apiKey = "41340719-bdedc7ab9607927bbd8e187bd"
        val query = "live wallpapers"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitPixabay.service.getVideos(apiKey, query)
                handleResponse(response)
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    private fun handleResponse(response: Response<PixabayVideoResponse>) {
        if (response.isSuccessful) {
            val pixabayResponse = response.body()
            pixabayResponse?.let {
                _wallpaperData.postValue(it.hits)
            }
        }
        _networkRequestStatus.postValue(false)
    }

    private fun handleError() {
        _networkRequestStatus.postValue(false)
        // Handle network errors
    }

    fun clear() {
        _wallpaperData.value = null
    }
}
