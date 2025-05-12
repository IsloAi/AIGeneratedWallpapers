package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetAllWallpapersUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetChargingAnimationsUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetDoubleWallpaperUseCase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.GetLiveWallpapersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

//Created on  by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

@HiltViewModel
class FetchAllWallpapersViewModel @Inject constructor(
    private val getAllWallpapersUseCase: GetAllWallpapersUseCase,
    private val getLiveWallpapersUseCase: GetLiveWallpapersUseCase,
    private val getChargingAnimations: GetChargingAnimationsUseCase,
    private val getDoubleWallpaperUseCase: GetDoubleWallpaperUseCase
) : ViewModel() {

    init {
        Log.d("TAG", "Init Called")

        fetchAndCacheWallpapers()
    }


    private fun fetchAndCacheWallpapers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val all = getAllWallpapersUseCase()
                val live = getLiveWallpapersUseCase()
                val charging = getChargingAnimations()
                val double = getDoubleWallpaperUseCase()

                all.collect { response ->
                    if (response.isSuccessful) {
                        Log.d("FetchVM", "✅ All Static wallpaper data fetched and saved to DB")
                        Log.d("FetchVM", "✅ Response: ${response.body()}")
                    } else {
                        Log.d("FetchVM", "❌ Failed to fetch/save wallpapers")
                    }

                }

                live.collect { response ->
                    if (response.isSuccessful) {
                        Log.d("FetchVM", "✅ Live wallpaper data fetched and saved to DB")
                        Log.d("FetchVM", "✅ Response: ${response.body()}")
                    }
                }

                charging.collect { response ->
                    if (response.isSuccessful) {
                        Log.d("FetchVM", "✅ Charging wallpaper data fetched and saved to DB")
                        Log.d("FetchVM", "✅ Response: ${response.body()}")
                    }
                }

                double.collect { response ->
                    if (response.isSuccessful) {
                        Log.d("FetchVM", "✅ Double wallpaper data fetched and saved to DB")
                        Log.d("FetchVM", "✅ Response: ${response.body()}")
                    }
                }

                // Assuming SaveWallpapersUseCase takes a list of wallpapers and a category tag
                /*saveWallpapersUseCase(all, "All")
                saveWallpapersUseCase(live, "Live")
                saveWallpapersUseCase(charging, "Charging")
                saveWallpapersUseCase(double, "Double")*/

                Log.d("FetchVM", "✅ All wallpaper data fetched and saved to DB")

            } catch (e: Exception) {
                Log.e("FetchVM", "❌ Failed to fetch/save wallpapers", e)
            }
        }
    }
}