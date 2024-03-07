package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils
import android.util.Log
import android.view.View.GONE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.CatResponseInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCatNameViewModel: ViewModel()  {

    private var _wallpaperData = MutableLiveData<List<CatNameResponse>>()
    val wallpaper:LiveData<List<CatNameResponse>> = _wallpaperData
    fun fetchWallpapers() {
        viewModelScope.launch(Dispatchers.IO) {
            val retrofit = RetrofitInstance.getInstance()
            val service = retrofit.create(CatResponseInterface::class.java)
            val call: Call<ArrayList<CatNameResponse>> = service.getCategories()
            call.enqueue(object : Callback<ArrayList<CatNameResponse>> {
                override fun onResponse(call: Call<ArrayList<CatNameResponse>>, response: Response<ArrayList<CatNameResponse>>) {
                    if (response.isSuccessful) {
                        val catNameResponses: ArrayList<CatNameResponse>? = response.body()
                        if (catNameResponses != null) {
                            Log.e("TAG", "onResponse: $catNameResponses")
                            _wallpaperData.value = catNameResponses!!
                        }
                    } else {
                        // Handle error case
                        viewModelScope.launch {
                            val gson = Gson()
                            val categoryList: List<CatNameResponse> = gson.fromJson(categoriesJson, object : TypeToken<List<CatNameResponse>>() {}.type)

                            _wallpaperData.value = categoryList
                        }
                        Log.e("responseOk", "onResponse: response not Empty ")
                    }
                }

                override fun onFailure(call: Call<ArrayList<CatNameResponse>>, t: Throwable) {
                    // Handle failure case
                    Log.e("responseOk", "onResponse: response onFailure ")
                    viewModelScope.launch {
                        val gson = Gson()
                        val categoryList: List<CatNameResponse> = gson.fromJson(categoriesJson, object : TypeToken<List<CatNameResponse>>() {}.type)

                        _wallpaperData.value = categoryList
                    }
                }

            })
        }

    }


    val categoriesJson = """
[
    {
        "cat_name": "Vintage",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Vintage.jpg"
    },
    {
        "cat_name": "Tech",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Tech.jpg"
    },
    {
        "cat_name": "Dark",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Dark.jpg"
    },
    {
        "cat_name": "Black And White",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/white.jpg"
    },
    {
        "cat_name": "Architecture",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Architecture.jpg"
    },
    {
        "cat_name": "Minimal",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Minimal.jpg"
    },
    {
        "cat_name": "Abstract",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Abstract.jpg"
    },
    {
        "cat_name": "Motor Bike",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/bike.jpg"
    },
    {
        "cat_name": "Car",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Cars.jpg"
    },
    {
        "cat_name": "4K",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/4K.jpg"
    },
    {
        "cat_name": "Anime",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Anime.jpg"
    },
    {
        "cat_name": "IOS",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/IOS.jpg"
    },
    {
        "cat_name": "New Year",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/New-Year.jpg"
    },
    {
        "cat_name": "Christmas",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Christmas.jpg"
    },
    {
        "cat_name": "Fantasy",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Fantasy.jpg"
    },
    {
        "cat_name": "Artistic",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/artistic.jpg"
    },
    {
        "cat_name": "Pattern",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/64e4616cbd8e7_p3.png"
    },
    {
        "cat_name": "Space",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Space.jpg"
    },
    {
        "cat_name": "Super Heros",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Super-Heros.jpg"
    },
    {
        "cat_name": "Art",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Art.jpg"
    },
    {
        "cat_name": "Animals",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/64e460663ed90_animals.png"
    },
    {
        "cat_name": "City",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/City.jpg"
    },
    {
        "cat_name": "Nature",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Nature.jpg"
    },
    {
        "cat_name": "Ocean",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/ocean.jpg"
    },
    {
        "cat_name": "Travel",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Travel.jpg"
    },
    {
        "cat_name": "Love",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Love.jpg"
    },
    {
        "cat_name": "Sadness",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/sad.jpg"
    },
    {
        "cat_name": "Mountains",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Mountians.jpg"
    },
    {
        "cat_name": "Music",
        "img_url": "https://edecator.com/wallpaperApp/categoryimages/Music.jpg"
    }
]
"""
}