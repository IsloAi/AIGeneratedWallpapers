package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse


class SharedViewModel : ViewModel() {
    private val _catResponseList = MutableLiveData<List<CatResponse>>()
    val catResponseList: LiveData<List<CatResponse>> = _catResponseList

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    fun setData(catResponses: List<CatResponse>, position: Int) {
        _catResponseList.value = catResponses
        _currentPosition.value = position
    }

    fun clearData() {
        _catResponseList.value = emptyList()
    }
}