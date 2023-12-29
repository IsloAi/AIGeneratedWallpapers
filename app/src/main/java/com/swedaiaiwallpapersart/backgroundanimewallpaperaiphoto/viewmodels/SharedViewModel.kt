package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel


class SharedViewModel : ViewModel() {
    private val _catResponseList = MutableLiveData<List<CatResponse>>()
    val catResponseList: LiveData<List<CatResponse>> = _catResponseList

    private val _liveWallpaperResponseList = MutableLiveData<List<LiveWallpaperModel>>()
    val liveWallpaperResponseList: LiveData<List<LiveWallpaperModel>> = _liveWallpaperResponseList

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private val _currentPositionViewWall = MutableLiveData<Int>()
    val currentPositionViewWall: LiveData<Int> = _currentPositionViewWall

    private val _selectTab = MutableLiveData<Int>()
    val selectTab: LiveData<Int> = _selectTab

    val selectedCat = MutableLiveData<CatResponse>()

    fun setData(catResponses: List<CatResponse>, position: Int) {
        _catResponseList.value = catResponses
        _currentPosition.value = position
    }

    fun setLiveWallpaper(catResponses: List<LiveWallpaperModel>){
        _liveWallpaperResponseList.value = catResponses
    }

    fun clearData() {
        _catResponseList.value = emptyList()
    }

    fun clearLiveWallpaper() {
        _liveWallpaperResponseList.value = emptyList()
    }

    fun selectCat(cat: CatResponse) {
        selectedCat.value = cat
    }

    fun setPosition(position: Int){
        _currentPositionViewWall.value = position
    }

    fun selectTab(position: Int){
        _selectTab.value = position
    }







}