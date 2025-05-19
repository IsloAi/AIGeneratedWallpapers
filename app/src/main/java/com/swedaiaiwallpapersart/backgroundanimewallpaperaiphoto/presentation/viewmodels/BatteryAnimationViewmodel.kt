package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel

class BatteryAnimationViewmodel : ViewModel() {


    private val _chargingAdPosition = MutableLiveData<Int>()
    val chargingAdPosition: LiveData<Int> = _chargingAdPosition


    private val _chargingAnimationResponseList = MutableLiveData<List<ChargingAnimModel>>()
    val chargingAnimationResponseList: LiveData<List<ChargingAnimModel>> =
        _chargingAnimationResponseList

    fun setchargingAnimation(catResponses: List<ChargingAnimModel>) {
        _chargingAnimationResponseList.value = catResponses
    }

    fun clearChargeAnimation() {
        _chargingAnimationResponseList.value = emptyList()
    }

    fun setChargingAdPosition(position: Int) {
        _chargingAdPosition.value = position
    }
}