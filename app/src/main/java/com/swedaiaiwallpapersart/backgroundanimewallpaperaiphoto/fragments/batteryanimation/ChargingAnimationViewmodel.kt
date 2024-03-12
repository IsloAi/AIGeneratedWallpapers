package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetChargingAnimRemoteUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChargingAnimationViewmodel@Inject constructor(
    private val getChargingAnimRemoteUseCase: GetChargingAnimRemoteUseCase
):  ViewModel() {

    private var _chargingAnimList= MutableLiveData<Response<ArrayList<ChargingAnimModel>>>(Response.Success(null))
    val chargingAnimList: LiveData<Response<ArrayList<ChargingAnimModel>>> = _chargingAnimList

    fun getChargingAnimations(){
        viewModelScope.launch {
            getChargingAnimRemoteUseCase.invoke().collect(){
                Log.e("TAG", "getChargingAnimation: $it")
                _chargingAnimList.value=it
            }
        }
    }

}