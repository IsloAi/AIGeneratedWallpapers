package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
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


    private val _liveAdPosition = MutableLiveData<Int>()
    val liveAdPosition: LiveData<Int> = _liveAdPosition

    private val _chargingAdPosition = MutableLiveData<Int>()
    val chargingAdPosition: LiveData<Int> = _chargingAdPosition

    private val _wallAdPosition = MutableLiveData<Int>()
    val wallAdPosition: LiveData<Int> = _wallAdPosition

    val selectedCat = MutableLiveData<CatResponse>()


    private val _chargingAnimationResponseList = MutableLiveData<List<ChargingAnimModel>>()
    val chargingAnimationResponseList: LiveData<List<ChargingAnimModel>> = _chargingAnimationResponseList

    fun setData(catResponses: List<CatResponse>, position: Int) {
        _catResponseList.value = catResponses
        _currentPosition.value = position
    }




    fun updateCatResponseAtIndex(updatedCatResponse: CatResponse, index: Int) {
        val currentList = _catResponseList.value.orEmpty().toMutableList()
        if (index in 0 until currentList.size) {
            currentList[index] = updatedCatResponse
            _catResponseList.value = currentList
        }
    }

    fun setLiveWallpaper(catResponses: List<LiveWallpaperModel>){
        _liveWallpaperResponseList.value = catResponses
    }

    fun setchargingAnimation(catResponses: List<ChargingAnimModel>){
        _chargingAnimationResponseList.value = catResponses
    }

    fun clearData() {
        _catResponseList.value = emptyList()
    }

    fun clearLiveWallpaper() {
        _liveWallpaperResponseList.value = emptyList()
    }

    fun clearChargeAnimation() {
        _chargingAnimationResponseList.value = emptyList()
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


    fun setAdPosition(position: Int){
        _liveAdPosition.value = position
    }


    fun setChargingAdPosition(position: Int){
        _chargingAdPosition.value = position
    }

    fun setWallAdPosition(position: Int){
        _wallAdPosition.value = position
    }






}