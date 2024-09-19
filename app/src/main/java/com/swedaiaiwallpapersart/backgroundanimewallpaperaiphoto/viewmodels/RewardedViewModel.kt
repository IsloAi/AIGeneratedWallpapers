package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.RewardedAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetRewardWallpaperUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardedViewModel@Inject constructor(private val getRewardWallpaperUseCase: GetRewardWallpaperUseCase):ViewModel() {

    private var _allWallpapers = MutableLiveData<Response<List<RewardedAllResponse>>>(
        Response.Success(
            emptyList()
        ))
    val allWallpapers: LiveData<Response<List<RewardedAllResponse>>> = _allWallpapers

    init {
        getAllWallpapers()
    }
    fun getAllWallpapers(){
        viewModelScope.launch {
            getRewardWallpaperUseCase.invoke().collect{
                _allWallpapers.value=it
            }
        }
    }
}