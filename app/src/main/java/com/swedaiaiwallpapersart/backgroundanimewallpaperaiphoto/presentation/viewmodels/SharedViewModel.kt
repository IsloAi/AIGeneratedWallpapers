package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse


class SharedViewModel : ViewModel() {
    private val _catResponseList = MutableLiveData<List<SingleDatabaseResponse>>()
    val catResponseList: LiveData<List<SingleDatabaseResponse>> = _catResponseList

    private val _liveWallpaperResponseList = MutableLiveData<List<LiveWallpaperModel>>()
    val liveWallpaperResponseList: LiveData<List<LiveWallpaperModel>> = _liveWallpaperResponseList

    private val _favLiveWallpaperResponseList = MutableLiveData<List<LiveWallpaperModel>>()
    val favLiveWallpaperResponseList: LiveData<List<LiveWallpaperModel>> =
        _favLiveWallpaperResponseList

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private val _currentPositionViewWall = MutableLiveData<Int>()
    val currentPositionViewWall: LiveData<Int> = _currentPositionViewWall

    private val _selectTab = MutableLiveData<Int>()
    val selectTab: LiveData<Int> = _selectTab


    private val _liveAdPosition = MutableLiveData<Int>()
    val liveAdPosition: LiveData<Int> = _liveAdPosition

    private val _chargingAdPosition = MutableLiveData<Int>()
    val chargingAdPosition: LiveData<Int> = _chargingAdPosition

    private val _wallAdPosition = MutableLiveData<Int>()
    val wallAdPosition: LiveData<Int> = _wallAdPosition

    private val _wallpaperFromType = MutableLiveData<String>()
    val wallpaperFromType: LiveData<String> = _wallpaperFromType

    val selectedCat = MutableLiveData<SingleDatabaseResponse>()

    private val _chargingAnimationResponseList = MutableLiveData<List<ChargingAnimModel>>()
    val chargingAnimationResponseList: LiveData<List<ChargingAnimModel>> =
        _chargingAnimationResponseList

    fun setData(catResponses: List<SingleDatabaseResponse>, position: Int) {
        Log.e("HOMEFRAG", "setData: Setting new list in viewmodel,${catResponses.size}")
        _catResponseList.value = catResponses
        _currentPosition.value = position
    }

    fun setCatResponseList(catResponses: List<SingleDatabaseResponse>) {
        _catResponseList.value = catResponses
    }

    fun addNullValuesToCatResponseList(catResponses: List<CatResponse?>): List<CatResponse?> {
        val listWithNulls = catResponses.toMutableList()
        listWithNulls.add(null)
        return listWithNulls
    }

    fun updateCatResponseAtIndex(updatedCatResponse: SingleDatabaseResponse, index: Int) {
        val currentList = _catResponseList.value.orEmpty().toMutableList()
        if (index in 0 until currentList.size) {
            currentList[index] = updatedCatResponse
            _catResponseList.value = currentList
        }
    }

    fun setLiveWallpaper(catResponses: List<LiveWallpaperModel>) {
        _liveWallpaperResponseList.value = catResponses
    }

    fun setFavLiveWallpaper(catResponses: List<LiveWallpaperModel>) {
        _favLiveWallpaperResponseList.value = catResponses
    }

    fun clearData() {
        _catResponseList.value = emptyList()
    }

    fun clearLiveWallpaper() {
        _liveWallpaperResponseList.value = emptyList()
    }

    fun selectCat(cat: SingleDatabaseResponse) {
        selectedCat.value = cat
    }

    fun setPosition(position: Int) {
        _currentPositionViewWall.value = position
    }

    fun selectTab(position: Int) {
        _selectTab.value = position
    }

    fun setAdPosition(position: Int) {
        _liveAdPosition.value = position
    }

    fun setWallpaperFromType(type: String) {
        _wallpaperFromType.value = type
    }

    fun getWallpaperFromType(): String? {
        return _wallpaperFromType.value
    }

}