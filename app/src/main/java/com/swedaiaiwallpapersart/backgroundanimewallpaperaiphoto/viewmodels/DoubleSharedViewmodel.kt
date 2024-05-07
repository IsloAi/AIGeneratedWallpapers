package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.util.Log
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


    fun updateDoubleWallById(id: Int, updatedItem: DoubleWallModel) {
        val currentList = _doubleWallResponseList.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = updatedItem
            Log.e("TAG", "updateDoubleWallById: "+updatedItem )
            _doubleWallResponseList.value = currentList
        }
    }

    fun setDoubleWalls(doubleWalls: List<DoubleWallModel>){
        _doubleWallResponseList.value = doubleWalls
    }
    fun clearChargeAnimation() {
        _chargingAnimationResponseList.value = emptyList()
    }
}