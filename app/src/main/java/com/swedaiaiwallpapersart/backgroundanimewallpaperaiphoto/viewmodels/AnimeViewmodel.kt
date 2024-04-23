package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetCategoryWallpapersUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ListResponseInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class AnimeViewmodel @Inject constructor(private val getCategoryWallpapersUseCase: GetCategoryWallpapersUseCase): ViewModel()  {
    private var _catWalls = MutableLiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<List<SingleDatabaseResponse>>>(
        com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response.Success(
            emptyList()
        ))
    val catWallpapers: LiveData<com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response<List<SingleDatabaseResponse>>> = _catWalls

    fun getAllCreations(cat:String){
        viewModelScope.launch {
            getCategoryWallpapersUseCase.invoke(cat).collect(){
                _catWalls.value=it
            }
        }
    }
}