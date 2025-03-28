package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetAllLikedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFavouriteViewModel @Inject constructor(
    private val getFavouritesUseCase: GetAllLikedUseCase
) : ViewModel() {

    private val _favourites = MutableStateFlow<Response<ArrayList<String>>>(Response.Loading)
    val favourites: StateFlow<Response<ArrayList<String>>> = _favourites

    fun loadFavourites(deviceId: String) {
        viewModelScope.launch {
            getFavouritesUseCase(deviceId).collect { response ->
                when (response) {
                    is Response.Success -> {
                        // Convert to ArrayList before setting the value
                        val ids = response.data?.map { it.imageid }?.let { ArrayList(it) } ?: ArrayList()
                        Log.d("FAVORITES", "loadFavourites: $ids")
                        _favourites.value = Response.Success(ids)
                    }

                    is Response.Loading -> {
                        _favourites.value = Response.Loading
                    }

                    is Response.Error -> {
                        _favourites.value = Response.Error(response.message)
                    }

                    is Response.Processing -> {
                        // Handle processing state if needed
                    }
                }
            }

        }
    }

}