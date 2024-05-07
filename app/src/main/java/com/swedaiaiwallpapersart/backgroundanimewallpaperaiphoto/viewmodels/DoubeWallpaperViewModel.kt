package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetDoubleWallpapersUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DoubeWallpaperViewModel@Inject constructor(
    private val getDoubleWallpapersUseCase: GetDoubleWallpapersUseCase
):  ViewModel() {

    private var _doubleWallList= MutableLiveData<Response<ArrayList<DoubleWallModel>>>(Response.Success(null))
    val doubleWallList: LiveData<Response<ArrayList<DoubleWallModel>>> = _doubleWallList

    fun getDoubleWallpapers(){
        viewModelScope.launch {
            getDoubleWallpapersUseCase.invoke().collect(){
                Log.e("TAG", "getChargingAnimation: $it")
                _doubleWallList.value=it
            }
        }
    }

    fun updateValueById(id: Int, newValue: DoubleWallModel) {
        val currentResponse = _doubleWallList.value
        if (currentResponse is Response.Success) {
            val currentList = currentResponse.data ?: return // Extract current list
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                val updatedList = ArrayList(currentList) // Create a copy of the current list
                updatedList[index] = newValue // Update the value at the found index
                _doubleWallList.value = Response.Success(updatedList) // Update the LiveData
            }
        }
    }



}