package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.TokenResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.FetechAllWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GenerateDeviceTokenUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel@Inject constructor(private val generateDeviceTokenUsecase: GenerateDeviceTokenUsecase
,private val fetechAllWallpapersUsecase: FetechAllWallpapersUsecase):  ViewModel()  {

    private var _allModels= MutableLiveData<Response<ArrayList<SingleAllResponse>>>(Response.Success(null))
    val allModels: LiveData<Response<ArrayList<SingleAllResponse>>> = _allModels


    private var _devicetokenResponse= MutableStateFlow<Response<TokenResponse>>(Response.Success(null))

    val deviceTokenResponse: StateFlow<Response<TokenResponse>> =_devicetokenResponse

    fun generateDeviceToken(deviceId: String){
        viewModelScope.launch {
            generateDeviceTokenUsecase.invoke(deviceId).collect(){
                _devicetokenResponse.value=it
            }
        }
    }

    fun getAllModels(api:String,page:String,record:String){
        viewModelScope.launch {
            fetechAllWallpapersUsecase.invoke(api,page,record).collect(){
                Log.e("TAG", "getAllModels: "+it )
                _allModels.value=it
            }
        }
    }
}