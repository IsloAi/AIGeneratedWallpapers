package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetAllWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetTrendingWallpaperUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MostDownloadedViewmodel@Inject constructor(private val getAllWallpapersUsecase: GetAllWallpapersUsecase,
                                                 private val getTrendingWallpaperUseCase: GetTrendingWallpaperUseCase): ViewModel()  {

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

}