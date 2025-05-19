package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repository.GetWallsFromRoomRepoImplementation
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//Created on  by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

@HiltViewModel
class DataFromRoomViewmodel @Inject constructor(private val repo: GetWallsFromRoomRepoImplementation) :
    ViewModel() {

    private val _wallpapers = MutableStateFlow<List<SingleDatabaseResponse>>(emptyList())
    val allWallpapers: StateFlow<List<SingleDatabaseResponse>> = _wallpapers

    private val _trendingWallpapers = MutableStateFlow<List<SingleDatabaseResponse>>(emptyList())
    val trendingWallpapers: StateFlow<List<SingleDatabaseResponse>> = _trendingWallpapers

    private val _popularWallpapers = MutableStateFlow<List<SingleDatabaseResponse>>(emptyList())
    val popularWallpapers: StateFlow<List<SingleDatabaseResponse>> = _popularWallpapers

    private val _liveWallpapers = MutableStateFlow<List<LiveWallpaperModel>>(emptyList())
    val liveWallpapers: StateFlow<List<LiveWallpaperModel>> = _liveWallpapers

    private val _doubleWallpapers = MutableStateFlow<List<DoubleWallModel>>(emptyList())
    val doubleWallpapers: StateFlow<List<DoubleWallModel>> = _doubleWallpapers

    private val _carWallpapers = MutableStateFlow<List<SingleDatabaseResponse>>(emptyList())
    val carWallpapers: StateFlow<List<SingleDatabaseResponse>> = _carWallpapers

    private val _categoryWallpapers = MutableStateFlow<List<SingleDatabaseResponse>>(emptyList())
    val categoryWallpapers: StateFlow<List<SingleDatabaseResponse>> = _categoryWallpapers

    private val _chargingAnimations = MutableStateFlow<List<ChargingAnimModel>>(emptyList())
    val chargingAnimations: StateFlow<List<ChargingAnimModel>> = _chargingAnimations

    private val _liveCatWallpapers = MutableStateFlow<List<LiveWallpaperModel>>(emptyList())
    val liveCategoryWallpapers: StateFlow<List<LiveWallpaperModel>> = _liveCatWallpapers

    private val _liveFavWallpapers = MutableStateFlow<List<LiveWallpaperModel>>(emptyList())
    val liveFavouriteWallpapers: StateFlow<List<LiveWallpaperModel>> = _liveFavWallpapers

    private val _staticFavWallpapers = MutableStateFlow<List<SingleDatabaseResponse>>(emptyList())
    val staticFavouriteWallpapers: StateFlow<List<SingleDatabaseResponse>> = _staticFavWallpapers

    fun fetchAllStaticWallpapers() {
        viewModelScope.launch {
            _wallpapers.value = repo.getAllWallpapersFromRoom()
        }
    }

    fun fetchTrendingWallpapers() {
        viewModelScope.launch {
            _trendingWallpapers.value = repo.getTrendingWallpapersFromRoom()
        }
    }

    fun fetchPopularWallpapers() {
        viewModelScope.launch {
            _popularWallpapers.value = repo.getPopularWallpapersFromRoom()
        }

    }

    fun fetchAllLiveWallpapers() {
        viewModelScope.launch {
            _liveWallpapers.value = repo.getAllLiveWallpapersFromRoom()
        }

    }

    fun fetchAllDoubleWallpapers() {
        viewModelScope.launch {
            _doubleWallpapers.value = repo.getAllDoubleWallpapersFromRoom()
        }
    }

    fun fetchCarWallpapers() {
        viewModelScope.launch {
            _carWallpapers.value = repo.getCarWallpapersFromRoom()
        }
    }

    fun fetchCategoryWallpapers(cat: String) {
        viewModelScope.launch {
            _categoryWallpapers.value = repo.getCategoryWallpaperFromRoom(cat)
        }
    }

    fun fetchChargingAnimations() {
        viewModelScope.launch {
            _chargingAnimations.value = repo.getBatteryWallpapersFromRoom()
        }
    }

    fun fetchLiveCategoryWallpapers(cat: String) {
        viewModelScope.launch {
            _liveCatWallpapers.value = repo.getLiveCategoryWallpaperFromRoom(cat)
        }
    }

    fun updateLiveFavourite(liked: Boolean, Id: Int) {
        viewModelScope.launch {
            repo.updateLiveFavourite(liked, Id)
        }
    }

    fun getLiveFavourites() {
        viewModelScope.launch {
            _liveFavWallpapers.value = repo.getLiveFavourites()
        }
    }

    fun updateStaticFavourite(liked: Boolean, Id: Int) {
        viewModelScope.launch {
            repo.updateStaticFavourite(liked, Id)
        }
    }

    fun getStaticFavourites() {
        viewModelScope.launch {
            _staticFavWallpapers.value = repo.getStaticFavourites()
        }

    }

}