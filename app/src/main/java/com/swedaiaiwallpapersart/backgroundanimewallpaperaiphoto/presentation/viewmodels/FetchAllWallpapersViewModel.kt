package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse
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

    @Inject
    lateinit var appDatabase: AppDatabase

    init {
        //Log.d("TAG", "Init Called")
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
                        response.body()?.let { wallpapersList ->
                            // Assuming wallpapersList is List<ApiWallpaper> and you map it to List<SingleDatabaseResponse>
                            val entityList = wallpapersList.images.map { apiWallpaper ->
                                val single = SingleDatabaseResponse(
                                    id = apiWallpaper.id,
                                    cat_name = apiWallpaper.cat_name,
                                    image_name = apiWallpaper.image_name,
                                    hd_image_url = apiWallpaper.url,
                                    compressed_image_url = apiWallpaper.url,
                                    likes = apiWallpaper.likes,
                                    liked = apiWallpaper.liked,
                                    size = apiWallpaper.size,
                                    Tags = apiWallpaper.Tags,
                                    capacity = apiWallpaper.capacity,
                                    unlocked = true // Or map from API if dynamic
                                )
                                appDatabase.wallpapersDao().insert(single)
                            }
                            Log.d("FetchVM", "✅ Inserted ${entityList.size} wallpapers into DB")
                        }
                    } else {
                        Log.d("FetchVM", "❌ Failed to fetch/save wallpapers")
                    }
                }

                live.collect { response ->
                    if (response.isSuccessful) {
                        Log.d("FetchVM", "✅ Live wallpaper data fetched and saved to DB")
                        Log.d("FetchVM", "✅ Response: ${response.body()}")
                        response.body()?.let { wallpapersList ->
                            wallpapersList.liveWallpaper.map { apiWallpaper ->
                                val single = LiveWallpaperModel(
                                    id = apiWallpaper.id,
                                    livewallpaper_url = apiWallpaper.livewallpaper_url,
                                    thumnail_url = apiWallpaper.thumnail_url,
                                    videoSize = apiWallpaper.videoSize,
                                    liked = apiWallpaper.liked,
                                    downloads = apiWallpaper.downloads,
                                    catname = apiWallpaper.catname,
                                    likes = apiWallpaper.likes,
                                    unlocked = true
                                )
                                appDatabase.liveWallpaperDao().insert(single)
                            }
                        }
                    }
                }

                charging.collect { response ->
                    if (response.isSuccessful) {
                        Log.d("FetchVM", "✅ Charging wallpaper data fetched and saved to DB")
                        Log.d("FetchVM", "✅ Response: ${response.body()}")
                        response.body()?.let { wallpapersList ->
                            wallpapersList.chargingAnimations.map { apiWallpaper ->
                                val single = ChargingAnimModel(
                                    thumnail = apiWallpaper.thumnail,
                                    extension = apiWallpaper.extension,
                                    hd_animation = apiWallpaper.hd_animation
                                )
                                appDatabase.chargingAnimDao().insert(single)
                            }
                        }
                    }
                }

                double.collect { response ->
                    if (response.isSuccessful) {
                        Log.d("FetchVM", "✅ Double wallpaper data fetched and saved to DB")
                        Log.d("FetchVM", "✅ Response: ${response.body()}")
                        response.body()?.let { wallpapersList ->
                            wallpapersList.doubleWallpaper.map { apiWallpaper ->
                                val single = DoubleWallModel(
                                    id = apiWallpaper.id,
                                    hd_url1 = apiWallpaper.hd_url1,
                                    compress_url1 = apiWallpaper.compress_url1,
                                    size1 = apiWallpaper.size1,
                                    hd_url2 = apiWallpaper.hd_url2,
                                    compress_url2 = apiWallpaper.compress_url2,
                                    size2 = apiWallpaper.size2
                                )
                                appDatabase.doubleWallpaperDao().insert(single)
                            }
                        }

                    }
                }

                Log.d("FetchVM", "✅ All wallpaper data fetched and saved to DB")

            } catch (e: Exception) {
                Log.e("FetchVM", "❌ Failed to fetch/save wallpapers", e)
            }
        }
    }
}