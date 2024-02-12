package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils
import android.util.Log
import android.view.View.GONE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieAnimationView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.CatResponseInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                        Log.e("responseOk", "onResponse: response not Empty ")
                    }
                }

                override fun onFailure(call: Call<ArrayList<CatNameResponse>>, t: Throwable) {
                    // Handle failure case
                    Log.e("responseOk", "onResponse: response onFailure ")
                }

            })
        }

    }
}