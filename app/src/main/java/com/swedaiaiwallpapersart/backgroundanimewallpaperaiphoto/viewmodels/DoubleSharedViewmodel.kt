package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel


class DoubleSharedViewmodel : ViewModel() {

    private val _chargingAnimationResponseList = MutableLiveData<List<DoubleWallModel?>>()
    val chargingAnimationResponseList: LiveData<List<DoubleWallModel?>> = _chargingAnimationResponseList

    fun setchargingAnimation(catResponses: List<DoubleWallModel?>){
        _chargingAnimationResponseList.value = catResponses
    }


    private val _doubleWallResponseList = MutableLiveData<List<DoubleWallModel>>()
    val doubleWallResponseList: LiveData<List<DoubleWallModel>> = _doubleWallResponseList


    fun setDoubleWalls(doubleWalls: List<DoubleWallModel>){
        _doubleWallResponseList.value = doubleWalls
    }
    fun clearChargeAnimation() {
        _chargingAnimationResponseList.value = emptyList()
    }
}