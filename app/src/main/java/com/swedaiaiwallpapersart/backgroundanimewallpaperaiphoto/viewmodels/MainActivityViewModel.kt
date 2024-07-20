package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DeletedImagesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.LikesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.MostDownloadedResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleAllResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.TokenResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.FetechAllWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GenerateDeviceTokenUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetDeletedWallpaperImagesUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetMostUsedUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetStaticWallpaperUpdates
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetUpdatedWallpaperUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel@Inject constructor(
    private val getMostUsedUseCase: GetMostUsedUseCase,
    private val getUpdatedWallpaperUseCase: GetUpdatedWallpaperUseCase,
    private val getStaticWallpaperUpdates: GetStaticWallpaperUpdates,
    private val getDeletedWallpaperImagesUseCase: GetDeletedWallpaperImagesUseCase
    ):  ViewModel()  {

    private var _allModels= MutableLiveData<Response<ArrayList<SingleAllResponse>>>(Response.Success(null))
    val allModels: LiveData<Response<ArrayList<SingleAllResponse>>> = _allModels


    private var _updates= MutableLiveData<Response<ArrayList<SingleAllResponse>>>(Response.Success(null))
    val updates: LiveData<Response<ArrayList<SingleAllResponse>>> = _updates


    private var _mostUsed= MutableLiveData<Response<ArrayList<MostDownloadedResponse>>>(Response.Success(null))
    val mostUsed: LiveData<Response<ArrayList<MostDownloadedResponse>>> = _mostUsed



    fun getAllModels(page:String,record:String,lastid:String){
        viewModelScope.launch {
            getUpdatedWallpaperUseCase.invoke(page,record,lastid).collect(){
                Log.e("TAG", "getAllModels: "+it )
                _allModels.value=it
            }
        }
    }

    fun getStaticWallpaperUpdates() {
        viewModelScope.launch {
            getStaticWallpaperUpdates.invoke().collect() {
                Log.e("TAG", "getAllModels: " + it)
                _updates.value = it
            }
        }
    }

    private var _deletedIds = MutableLiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<ArrayList<DeletedImagesResponse>>>(
        com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(
            arrayListOf()
        ))
    val deletedIds: LiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<ArrayList<DeletedImagesResponse>>> = _deletedIds
    fun getDeletedImagesID(){
        viewModelScope.launch {
            getDeletedWallpaperImagesUseCase.invoke().collect(){
                Log.e("TAG", "getAllModels: "+it )
                _deletedIds.value=it

            }
        }
    }


    fun getMostUsed(page:String,record:String){
        viewModelScope.launch {
            getMostUsedUseCase.invoke(page,record).collect(){
                Log.e("TAG", "getAllModels: "+it )
                _mostUsed.value=it
            }
        }
    }
}